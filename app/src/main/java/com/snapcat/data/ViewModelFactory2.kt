package com.snapcat.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.snapcat.data.local.preferences.SettingPreferences
import com.snapcat.ui.dark_mode.ModeViewModel
import com.snapcat.ui.screen.splashscreen.SplashScreenViewModel

class ViewModelFactory2(private val pref: SettingPreferences) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ModeViewModel::class.java)) {
            return ModeViewModel(pref) as T
        }else if (modelClass.isAssignableFrom(SplashScreenViewModel::class.java)) {
            return SplashScreenViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}