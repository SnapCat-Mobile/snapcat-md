package com.snapcat.ui.screen.auth.forget

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.snapcat.R
import com.snapcat.data.ResultMessage
import com.snapcat.data.ViewModelFactory
import com.snapcat.data.model.User
import com.snapcat.data.remote.response.ResponseLogin
import com.snapcat.databinding.FragmentBottomForgetPasswordBinding
import com.snapcat.ui.screen.MainActivity
import com.snapcat.ui.screen.auth.AuthViewModel
import com.snapcat.ui.screen.auth.verifikasi.VerifikasiDialogFragment
import com.snapcat.util.ToastUtils
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class ForgetDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var binding: FragmentBottomForgetPasswordBinding? = null
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomForgetPasswordBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        binding?.forget?.setOnClickListener(this)
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
            R.id.forget -> handleForget()
        }
    }

    private fun handleForget() {
        val email = binding?.edEmailForget?.text.toString()
        val data = User(email = email)

        val isEmailValid = binding?.edEmailForget?.error == null

        if (isEmailValid &&  email.isNotEmpty()) {
            viewModel.forgetPassword(data).observe(requireActivity()){ result ->
                handleResult(result)
            }
        } else {
            ToastUtils.showToast(requireActivity(), getString(R.string.data_invalid))
        }


    }

    private fun handleResult(result: ResultMessage<Response<ResponseBody>>){
        when (result) {
            is ResultMessage.Loading -> {
                showLoading(true)
            }
            is ResultMessage.Success -> {
                ToastUtils.showToast(requireActivity(), "New password sent to your email")
                showLoading(false)
                dismiss()

            }
            is ResultMessage.Error -> {
                val exception = result.exception
                val errorMessage = exception.message ?: "Error, please try again"
                ToastUtils.showToast(requireContext(), errorMessage)
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
