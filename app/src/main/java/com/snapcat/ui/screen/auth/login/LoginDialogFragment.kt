package com.snapcat.ui.screen.auth.login

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.snapcat.R
import com.snapcat.data.ResultMessage
import com.snapcat.data.ViewModelFactory
import com.snapcat.data.local.preferences.UserDataStore
import com.snapcat.data.model.User
import com.snapcat.data.remote.response.ResponseLogin
import com.snapcat.databinding.FragmentBottomLoginBinding
import com.snapcat.ui.screen.MainActivity
import com.snapcat.ui.screen.auth.AuthViewModel
import com.snapcat.ui.screen.auth.forget.ForgetDialogFragment
import com.snapcat.ui.screen.auth.register.RegisterDialogFragment
import com.snapcat.ui.screen.onboarding.OnBoarding
import com.snapcat.util.ToastUtils
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class LoginDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var binding: FragmentBottomLoginBinding? = null
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var userDataStore: UserDataStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        userDataStore = UserDataStore.getInstance(requireContext())
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet: FrameLayout = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

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
        binding?.forgetPassword?.setOnClickListener(this)
        binding?.login?.setOnClickListener(this)
        binding?.forgetPassword?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding?.closeLogin?.setOnClickListener {
            startActivity(Intent(requireActivity(), OnBoarding::class.java))
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
        binding?.toSignUp?.setOnClickListener {
            dismiss()
            val registerDialog = RegisterDialogFragment()
            registerDialog.show((context as AppCompatActivity).supportFragmentManager, "RegisterDialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.forget_password -> {
                val forgetDialog = ForgetDialogFragment()
                forgetDialog.show((context as AppCompatActivity).supportFragmentManager, "ForgetDialog")
            }
            R.id.login -> handleLogin()
        }
    }

    private fun handleLogin() {
        binding?.apply {
            val email = edEmailLogin.text.toString()
            val password = edPasswordLogin.text.toString()

            val data = User(email = email, password = password)

            val isEmailValid = edEmailLogin.error == null
            val isPasswordValid = edPasswordLogin.error == null

            if (isEmailValid && isPasswordValid && email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(data).observe(requireActivity()) { result ->
                    handleResult(result)
                }
            } else {
                ToastUtils.showToast(requireActivity(), getString(R.string.data_invalid))
            }

        }
    }

    private fun handleResult(result: ResultMessage<ResponseLogin>){
        when (result) {
            is ResultMessage.Loading -> {
                showLoading(true)
            }
            is ResultMessage.Success -> {
                val response = ResponseLogin(data = result.data.data, message = result.data.message)

                val userId: String? = response.data.user.id
                val username: String? = response.data.user.username
                val email: String? = response.data.user.email
                val token: String = response.data.accessToken
                lifecycleScope.launch {
                    userDataStore.saveUserData(userId, username, token, email)
                }
                showLoading(false)
                requireActivity().finish()
                dismiss()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                ToastUtils.showToast(requireActivity(), "Log in Successfully, Welcome ${username} !")
            }
            is ResultMessage.Error -> {
                val exception = result.exception
                val errorMessage = exception.message ?: "Login gagal, silahkan coba lagi"
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
