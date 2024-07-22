package com.snapcat.data.remote.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//class ApiConfig {
//    companion object{
//        fun getApiService(): ApiService {
//            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
//            val authInterceptor = Interceptor { chain ->
//                val req = chain.request()
//                val requestHeaders = req.newBuilder()
//                    .build()
//                chain.proceed(requestHeaders)
//            }
//            val client = OkHttpClient.Builder()
//                .addInterceptor(loggingInterceptor)
//                .addInterceptor(authInterceptor)
//                .build()
//            val retrofit = Retrofit.Builder()
//                .baseUrl("https://snapcat-api-fgyzs52kkq-uc.a.run.app/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
//                .build()
//            return retrofit.create(ApiService::class.java)
//        }
//    }
//}

object ApiConfig {
    private const val BASE_URL_CC = "https://snapcat-api-fgyzs52kkq-uc.a.run.app/"
    private const val BASE_URL_ML = "https://snapcat-image-fgyzs52kkq-uc.a.run.app/"

    private val loggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)


    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    fun getApiServiceCC(): ApiServiceCC {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_CC)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiServiceCC::class.java)
    }

    fun getApiServiceML(): ApiServiceML {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_ML)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiServiceML::class.java)
    }

    fun getCombinedApiService(
        apiServiceCC: ApiServiceCC,
        apiServiceML: ApiServiceML,
    ): CombinedApiService {
        return CombinedApiServiceImpl(apiServiceCC, apiServiceML)
    }
}
