package com.snapcat.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.snapcat.di.Injection
import com.snapcat.ui.dark_mode.ModeViewModel
import com.snapcat.ui.screen.auth.AuthViewModel
import com.snapcat.ui.screen.detail.DetailViewModel
import com.snapcat.ui.screen.home.HomeViewModel
import com.snapcat.ui.screen.journey.JourneyViewModel
import com.snapcat.ui.screen.profile.ProfileViewModel
import com.snapcat.ui.screen.scan.ScanViewModel
import com.snapcat.ui.screen.shop.ShopViewModel

class ViewModelFactory private constructor(private val snapCatRepository: SnapCatRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(snapCatRepository) as T
        }else if(modelClass.isAssignableFrom(ShopViewModel::class.java)){
            return ShopViewModel(snapCatRepository) as T
        }else if(modelClass.isAssignableFrom(JourneyViewModel::class.java)){
            return JourneyViewModel(snapCatRepository) as T
        }else if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(snapCatRepository) as T
        }else if(modelClass.isAssignableFrom(ScanViewModel::class.java)){
            return ScanViewModel(snapCatRepository) as T
        }else if(modelClass.isAssignableFrom(DetailViewModel::class.java)){
            return DetailViewModel(snapCatRepository) as T
        } else if(modelClass.isAssignableFrom(ProfileViewModel::class.java)){
            return ProfileViewModel(snapCatRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}