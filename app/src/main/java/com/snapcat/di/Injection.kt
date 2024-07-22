package com.snapcat.di

import android.content.Context
import android.util.Log
import com.snapcat.data.SnapCatRepository
import com.snapcat.data.local.preferences.UserDataStore
import com.snapcat.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
//    fun provideRepository(context: Context): SnapCatRepository {
//        //val userPreferences = UserPreferences.getInstance(context.dataStore)
//        val apiService = ApiConfig.getApiService()
//        return SnapCatRepository.getInstance(apiService)
//    }

    private var snapCatRepository: SnapCatRepository? = null

    fun provideRepository(context: Context): SnapCatRepository {
        if (snapCatRepository == null) {
            val pref = UserDataStore.getInstance(context)
            val apiServiceCC = ApiConfig.getApiServiceCC()
            val apiServiceML = ApiConfig.getApiServiceML()
            val combinedApiService = ApiConfig.getCombinedApiService(apiServiceCC, apiServiceML)
            snapCatRepository = SnapCatRepository.getInstance(combinedApiService)
        }
        return snapCatRepository!!
    }

    fun updateRepository(context: Context) {
        snapCatRepository = null
        provideRepository(context)
    }
}