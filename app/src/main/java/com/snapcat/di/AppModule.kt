//package com.snapcat.di
//
//import android.content.Context
//import androidx.room.Room
//import com.snapcat.BuildConfig
//import com.snapcat.data.SnapCatRepository
//import com.snapcat.data.local.database.SnapCatDao
//import com.snapcat.data.local.database.SnapCatDatabase
//import com.snapcat.data.remote.retrofit.ApiService
//import com.snapcat.data.remote.retrofit.ApiConfig
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object AppModule {
//
//    @Singleton
//    @Provides
//    fun provideAppDatabase(@ApplicationContext appContext: Context): SnapCatDatabase =
//        Room.databaseBuilder(
//            appContext,
//            SnapCatDatabase::class.java,
//            "db_snapcat"
//        ).fallbackToDestructiveMigration().build()
//
//    @Singleton
//    @Provides
//    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
//        .apply {
//            level = if(BuildConfig.DEBUG) {
//                HttpLoggingInterceptor.Level.BODY
//            } else {
//                HttpLoggingInterceptor.Level.NONE
//            }
//        }
//
//    @Singleton
//    @Provides
//    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
//        OkHttpClient
//            .Builder()
//            .addInterceptor(httpLoggingInterceptor)
//            .addInterceptor(ApiConfig())
//            .build()
//
//    @Singleton
//    @Provides
//    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
//        .addConverterFactory(GsonConverterFactory.create())
//        .baseUrl(BuildConfig.API_URL)
//        .client(okHttpClient)
//        .build()
//
//    @Singleton
//    @Provides
//    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
//
//    @Singleton
//    @Provides
//    fun provideSnapCatDao(database: SnapCatDatabase): SnapCatDao = database.snapCatDao()
//
//    @Singleton
//    @Provides
//    fun provideSnapCatRepository(apiService: ApiService, snapCatDao: SnapCatDao): SnapCatRepository = SnapCatRepository(apiService, snapCatDao)
//
//}