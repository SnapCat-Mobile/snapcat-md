package com.snapcat.ui.screen.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.snapcat.R
import com.snapcat.databinding.ActivityWelcomeBinding
import com.snapcat.ui.screen.onboarding.OnBoarding

class Welcome : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nextButton.setOnClickListener {
            startActivity(Intent(this@Welcome, OnBoarding::class.java))
            finish()
        }

    }
}