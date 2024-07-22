package com.snapcat.data.remote.retrofit

import com.snapcat.data.model.History
import com.snapcat.data.model.User
import com.snapcat.data.remote.response.ResponseGetAllHistories
import com.snapcat.data.remote.response.ResponseGetAllShop
import com.snapcat.data.remote.response.ResponseGetUser
import com.snapcat.data.remote.response.ResponseHistoryById
import com.snapcat.data.remote.response.ResponseLogin
import com.snapcat.data.remote.response.ResponseRegister
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiServiceCC {
    @POST("api/users/register")
    suspend fun register(@Body requestBody: User): ResponseRegister

    @POST("api/users/login")
    suspend fun login(@Body requestBody: User): ResponseLogin

    @POST("api/users/forgot-password/")
    suspend fun forgotPassword(@Body email: User): Response<ResponseBody>

    @POST("api/history/")
    suspend fun saveToHistory(@Header("Authorization") token: String ,@Body requestBody: History): Response<ResponseBody>

    @GET("api/users/{id}")
    suspend fun getUser(@Header("Authorization") token: String, @Path("id") id: String): ResponseGetUser

    @GET("api/shops/{id}")
    suspend fun getAllShop(@Header("Authorization") token: String, @Path("id") id: String): ResponseGetAllShop

    @GET("api/histories/{id}")
    suspend fun getAllHistories(@Header("Authorization") token: String, @Path("id") id: String): ResponseGetAllHistories

    @GET("api/history/{id}")
    suspend fun getHistoryById(@Header("Authorization") token: String, @Path("id") id: String): ResponseHistoryById
}