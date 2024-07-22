package com.snapcat.ui.screen.auth.register

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.snapcat.R
import com.snapcat.data.ResultMessage
import com.snapcat.data.ViewModelFactory
import com.snapcat.data.model.User
import com.snapcat.data.remote.response.ResponseLogin
import com.snapcat.data.remote.response.ResponseRegister
import com.snapcat.databinding.FragmentBottomRegisterBinding
import com.snapcat.ui.screen.auth.AuthViewModel
import com.snapcat.ui.screen.auth.forget.ForgetDialogFragment
import com.snapcat.ui.screen.auth.login.LoginDialogFragment
import com.snapcat.ui.screen.detail.DetailJourneyDialogFragment
import com.snapcat.util.ToastUtils
import dagger.hilt.android.AndroidEntryPoint

class RegisterDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var binding: FragmentBottomRegisterBinding? = null
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet: FrameLayout = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            requireView().isFocusableInTouchMode = true
            requireView().requestFocus()
            requireView().setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    dismiss()
                    return@setOnKeyListener true
                }
                false
            }
            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
        return dialog
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.register?.setOnClickListener(this)
        binding?.closeLogin?.setOnClickListener {
            dismiss()
        }
        binding?.toSignIn?.setOnClickListener {
            dismiss()
            val loginDialog = LoginDialogFragment()
            loginDialog.show((context as AppCompatActivity).supportFragmentManager, "LoginDialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.register -> handleRegister()
        }
    }

    private fun handleRegister() {
        binding?.apply {
            val username = edUsernameRegister.text.toString()
            val email = edEmailRegister.text.toString()
            val password = edPasswordRegister.text.toString()

            val data = User(username = username, email = email, password = password)

            val isEmailValid = edEmailRegister.error == null
            val isPasswordValid = edPasswordRegister.error == null
            val isUsernameValid = edUsernameRegister.error == null

            if (isEmailValid && isPasswordValid && isUsernameValid && username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.register(data).observe(requireActivity()) { result ->
                    handleResult(result)
                }
            } else {
                ToastUtils.showToast(requireActivity(), getString(R.string.data_invalid))
            }

        }
    }

    private fun handleResult(result: ResultMessage<ResponseRegister>){
        when (result) {
            is ResultMessage.Loading -> {
                showLoading(true)
            }
            is ResultMessage.Success -> {
                ToastUtils.showToast(requireActivity(), "Register berhasil")
                val response = ResponseRegister(data = result.data.data, message = result.data.message)
                showLoading(false)
                val loginDialogFragment = LoginDialogFragment()
                loginDialogFragment.show(
                    (context as AppCompatActivity).supportFragmentManager,
                    "LoginDialogFragment"
                )


            }
            is ResultMessage.Error -> {
                val exception = result.exception
                val errorMessage = exception.message ?: "Register gagal, silahkan coba lagi"
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
