package com.snapcat.ui.screen.onboarding

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.snapcat.R
import com.snapcat.data.SnapCatRepository
import com.snapcat.data.local.database.SnapCatDao
import com.snapcat.data.remote.retrofit.ApiService
import com.snapcat.databinding.ContentOnBoardingBinding
import com.snapcat.ui.screen.auth.AuthViewModel
import com.snapcat.ui.screen.auth.login.LoginDialogFragment
import com.snapcat.ui.screen.auth.register.RegisterDialogFragment


class OnboardingAdapter(private val context: Context) : RecyclerView.Adapter<OnboardingAdapter.ViewHolder>() {

    class ViewHolder(val binding: ContentOnBoardingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ViewHolder(ContentOnBoardingBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            if (position == 0) {
                signUpContent.visibility = android.view.View.INVISIBLE
                logInContent.visibility = android.view.View.INVISIBLE
                imageOnboarding.setImageResource(R.drawable.onboarding_1)
                titleContent.text = context.getString(R.string.title_on_boarding1)
                descContent.text = context.getString(R.string.desc1_onboarding)
            }
            if (position == 1) {
                imageOnboarding.setImageResource(R.drawable.onboarding_2)
                titleContent.text = context.getString(R.string.title_on_boarding2)
                descContent.text = context.getString(R.string.desc2_onboarding)

                logInContent.setOnClickListener {
                    val loginDialog = LoginDialogFragment()
                    loginDialog.show((context as AppCompatActivity).supportFragmentManager, "LoginDialog")
                }

                signUpContent.setOnClickListener {
                    val regsiterDialog = RegisterDialogFragment()
                    regsiterDialog.show((context as AppCompatActivity).supportFragmentManager, "RegisterDialog")
                }
            }
        }
    }
    override fun getItemCount() = 2
}
