package com.snapcat.ui.screen.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.snapcat.data.ViewModelFactory2
import com.snapcat.data.local.preferences.SettingPreferences
import com.snapcat.data.local.preferences.UserDataStore
import com.snapcat.data.local.preferences.dataStore
import com.snapcat.databinding.ActivitySplashScreenBinding
import com.snapcat.ui.screen.MainActivity
import com.snapcat.ui.screen.welcome.Welcome
import com.snapcat.util.Constants
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var userDataStore: UserDataStore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        userDataStore = UserDataStore.getInstance(this)
        val pref = SettingPreferences.getInstance(application.dataStore)
        val splashScreenViewModel =
            ViewModelProvider(this, ViewModelFactory2(pref))[SplashScreenViewModel::class.java]
        splashScreenViewModel.getThemeSettings().observe(this@SplashScreen) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        binding.SplashScreenImage.alpha = 0f
        binding.SplashScreenImage.animate().setDuration(Constants.ANIMATION_DURATION_SPLASHSCREEN).alpha(1f).withEndAction {
            lifecycleScope.launch {

                var nextIntent: Intent

                userDataStore.getUserData().collect { data ->
                    nextIntent = if (data.userId.isNotEmpty() && data.username.isNotEmpty() && data.token.isNotEmpty() && data.email.isNotEmpty()) {
                        Intent(this@SplashScreen, MainActivity::class.java)
                    } else {
                        Intent(this@SplashScreen, Welcome::class.java)
                    }
                    val options = ActivityOptionsCompat.makeCustomAnimation(this@SplashScreen, android.R.anim.fade_in, android.R.anim.fade_out)
                    startActivity(nextIntent, options.toBundle())
                    finish()
                }
            }
        }
    }
}