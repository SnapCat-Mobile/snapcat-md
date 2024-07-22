package com.snapcat.ui.screen.scan

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.snapcat.data.ResultMessage
import com.snapcat.data.ViewModelFactory
import com.snapcat.data.local.preferences.UserDataStore
import com.snapcat.data.remote.response.DataPrediction
import com.snapcat.data.remote.response.ResponsePrediction
import com.snapcat.databinding.FragmentScanBinding
import com.snapcat.util.Object
import com.snapcat.util.ToastUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScanFragment : Fragment(), OnDialogDismissListener {

    private lateinit var binding: FragmentScanBinding
    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(requireActivity()) }
    private val cameraSelector by lazy { CameraSelector.DEFAULT_BACK_CAMERA }
    private var imageCapture: ImageCapture? = null
    private var preview: Preview? = null
    private var isFrontCameraSelected = false
    private lateinit var userDataStore: UserDataStore
    private val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

    private val viewModelFactory: ViewModelFactory by lazy {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val viewModel: ScanViewModel by viewModels {
        viewModelFactory
    }

    private val cameraProviderResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                initializeCamera()
            } else {
                showPermissionSnackbar()
            }
        }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            handleGalleryResult(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraProviderResult.launch(Manifest.permission.CAMERA)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        userDataStore = UserDataStore.getInstance(requireActivity())
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (imageCapture == null || preview == null) {
            initializeCamera()
        }
    }

    private fun setClickListeners() {
        binding.shutter.setOnClickListener { takePicture() }
        binding.flash.setOnClickListener { toggleFlash() }
        binding.gallery.setOnClickListener { startGallery() }
        binding.mode.setOnClickListener { toggleCamera() }
    }

    @SuppressLint("RestrictedApi")
    private fun initializeCamera() {
        Log.d("ScanFragment", "Initializing camera...")
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val resolutionSelector = buildResolutionSelector()

            val newCameraSelector = if (isFrontCameraSelected) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            preview = buildPreview(resolutionSelector)
            imageCapture = buildImageCapture()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, newCameraSelector, preview!!, imageCapture!!)
                Log.d("ScanFragment", "Camera initialized successfully.")
            } catch (e: Exception) {
                Log.e("ScanFragment", "Use case binding failed: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    private fun toggleCamera() {
        isFrontCameraSelected = !isFrontCameraSelected

        releaseCamera()
        initializeCamera()
    }

    private fun buildResolutionSelector(): ResolutionSelector {
        return ResolutionSelector.Builder().setAspectRatioStrategy(
            AspectRatioStrategy(
                AspectRatio.RATIO_16_9,
                AspectRatioStrategy.FALLBACK_RULE_AUTO
            )
        ).build()
    }

    private fun buildPreview(resolutionSelector: ResolutionSelector): Preview {
        return Preview.Builder()
            .setResolutionSelector(resolutionSelector)
            .build()
            .also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }
    }

    private fun buildImageCapture(): ImageCapture {
        return ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    private fun releaseCamera() {
        imageCapture = null
        preview = null
        try {
            cameraProviderFuture.get()?.unbindAll()
        } catch (e: Exception) {
            Log.d("ScanFragment", "Error releasing camera: ${e.message}")
        }
    }

    private fun takePicture() {
        val file = createImageFile()
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture?.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(requireActivity()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    handleImageSaved(outputFileResults, file)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("ScanFragment", "Photo capture failed: ${exception.message}", exception)
                }
            })
    }

    private fun createImageFile(): File {
        return File(
            requireContext().getExternalFilesDirs(null)[0],
            "${System.currentTimeMillis()}.jpg"
        )
    }

    private fun handleImageSaved(outputFileResults: ImageCapture.OutputFileResults, file: File) {
        val savedUri = outputFileResults.savedUri ?: Uri.fromFile(file)

        viewModel.prediction(uriToFile(savedUri, requireContext())).observe(requireActivity()) {
            handleResult(it)
        }
    }

    private fun showPermissionSnackbar() {
        Snackbar.make(
            binding.root,
            "The camera permission is required",
            Snackbar.LENGTH_INDEFINITE
        ).show()
    }

    private fun handleResult(result: ResultMessage<ResponsePrediction>) {

        when (result) {
            is ResultMessage.Loading -> {
                showLoading(true)
            }

            is ResultMessage.Success -> {
                releaseCamera()
                ToastUtils.showToast(requireActivity(), "Berhasil")
                showLoading(false)

                val dataResult = result.data.data
                val data = DataPrediction(
                    dataResult.catBreedPredictions,
                    dataResult.uploadImage,
                    dataResult.catBreedDescription,
                    dataResult.confidence
                )

                Log.w("Scan Fragment", data.toString())
                val args = Bundle().apply {
                    putParcelable("data_prediction", data)
                    putBoolean("is_from_scan", true)
                }

                val resultDialog = Object.newInstanceFragmentResultScan(this@ScanFragment).apply {
                    arguments = args
                }
                resultDialog.show(
                    (context as AppCompatActivity).supportFragmentManager,
                    "ResultScanDialog"
                )

            }

            is ResultMessage.Error -> {
                val exception = result.exception
                val errorMessage = exception.message ?: "gagal, silahkan coba lagi"
                ToastUtils.showToast(requireContext(), errorMessage)
                showLoading(false)
            }

            else -> {
            }
        }
    }

    private fun handleGalleryResult(uri: Uri?) {
        if (uri != null) {
            Log.d("ScanFragment", "Gallery result URI: $uri")
            val file = uriToFile(uri, requireActivity())
            viewModel.prediction(file).observe(requireActivity()) {
                handleResult(it)
            }

        } else {
            Log.e("ScanFragment", "Failed to retrieve URI from gallery result.")
        }
    }


    private fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile(timeStamp, ".jpg", filesDir)
    }

    private fun uriToFile(imageUri: Uri, context: Context): File {
        val myFile = createCustomTempFile(context)
        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(
            buffer,
            0,
            length
        )
        outputStream.close()
        inputStream.close()
        return myFile
    }


    private fun toggleFlash() {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            val torchState = camera.cameraInfo.torchState.value

            if (torchState == TorchState.ON) {
                camera.cameraControl.enableTorch(false)
            } else {
                camera.cameraControl.enableTorch(true)
            }
        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    override fun onDialogDismissed() {
        initializeCamera()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
