package com.snapcat.data.remote.retrofit

import com.snapcat.data.remote.response.ResponsePrediction
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiServiceML {
    @Multipart
    @POST("prediction")
    suspend fun prediction(
        @Part image: MultipartBody.Part
    ): ResponsePrediction
}
