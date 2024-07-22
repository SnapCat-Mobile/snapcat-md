package com.snapcat.ui.screen.detail

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.api.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.snapcat.data.ResultMessage
import com.snapcat.data.ViewModelFactory
import com.snapcat.data.local.preferences.UserDataStore
import com.snapcat.data.model.History
import com.snapcat.data.remote.response.DataPrediction
import com.snapcat.databinding.FragmentBottomDetailJourneyBinding
import com.snapcat.ui.screen.scan.OnDialogDismissListener
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class DetailJourneyDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var binding: FragmentBottomDetailJourneyBinding? = null
    var onDialogDismissed: OnDialogDismissListener? = null
    private lateinit var userDataStore: UserDataStore

    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomDetailJourneyBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userDataStore = UserDataStore.getInstance(requireActivity())
        val bottomSheet: FrameLayout = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.apply {
            peekHeight = resources.displayMetrics.heightPixels
            state = BottomSheetBehavior.STATE_EXPANDED

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {}

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }

        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                dismiss()
                return@setOnKeyListener true
            }
            false
        }

        val isFromScan = arguments?.getBoolean("is_from_scan", false) ?: false

        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("data_prediction", DataPrediction::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("data_prediction")
        }

        if (isFromScan) {
            lifecycleScope.launch {
                userDataStore.getUserData().collect{
                    val history = History(data!!.uploadImage, data.catBreedPredictions, data.catBreedDescription, it.userId)
                    viewModel.saveToHistory("Bearer ${it.token}" ,history).observe(viewLifecycleOwner) { result->
                        handleResult(result)
                    }
                }
            }
        }


        binding?.name?.text = data?.catBreedPredictions
        binding?.desc?.text = data?.catBreedDescription
        binding?.imageResult?.load(data?.uploadImage)

        binding?.closeLogin?.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDialogDismissed?.onDialogDismissed()
    }


    private fun handleResult(result: ResultMessage<Response<ResponseBody>>){
        when (result) {
            is ResultMessage.Loading -> {
                showLoading(true)
            }
            is ResultMessage.Success -> {
                showLoading(false)

            }
            is ResultMessage.Error -> {
//                val exception = result.exception
//                val errorMessage = exception.message ?: "failed to enter history"
//                ToastUtils.showToast(requireContext(), errorMessage)
                showLoading(false)
            }

            else -> {
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
         binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
