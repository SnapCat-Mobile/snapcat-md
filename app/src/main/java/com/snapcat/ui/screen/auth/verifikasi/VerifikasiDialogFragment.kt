package com.snapcat.ui.screen.auth.verifikasi

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.snapcat.R
import com.snapcat.databinding.FragmentBottomVerifikasiBinding
import com.snapcat.ui.screen.auth.forget.ForgetDialogFragment

class VerifikasiDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var binding: FragmentBottomVerifikasiBinding? = null

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.length == 1) {
                handleCodeFocus()
            }
        }

        override fun afterTextChanged(s: Editable) {
            if (s.isEmpty()) {
                handleCodeFocusEmpty()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomVerifikasiBinding.inflate(inflater, container, false)
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

        binding?.apply {
            code1.addTextChangedListener(textWatcher)
            code2.addTextChangedListener(textWatcher)
            code3.addTextChangedListener(textWatcher)
            code4.addTextChangedListener(textWatcher)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun handleCodeFocus() {
        val focusedCode = when {
            binding?.code1?.isFocused == true -> binding?.code2
            binding?.code2?.isFocused == true -> binding?.code3
            binding?.code3?.isFocused == true -> binding?.code4
            else -> null
        }

        focusedCode?.apply {
            clearFocus()
            requestFocus()
            isCursorVisible = true
        }
    }

    private fun handleCodeFocusEmpty() {
        val focusedCode = when {
            binding?.code2?.isFocused == true -> binding?.code1
            binding?.code3?.isFocused == true -> binding?.code2
            binding?.code4?.isFocused == true -> binding?.code3
            else -> null
        }

        focusedCode?.apply {
            clearFocus()
            requestFocus()
            isCursorVisible = true
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.forget_password -> {
                val forgetDialog = ForgetDialogFragment()
                forgetDialog.show((context as AppCompatActivity).supportFragmentManager, "ForgetDialog")
            }
        }
    }
}
