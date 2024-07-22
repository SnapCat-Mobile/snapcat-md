package com.snapcat.data.remote.retrofit

import com.snapcat.data.model.History
import com.snapcat.data.model.User
import com.snapcat.data.remote.response.ResponseGetAllHistories
import com.snapcat.data.remote.response.ResponseGetAllShop
import com.snapcat.data.remote.response.ResponseGetUser
import com.snapcat.data.remote.response.ResponseHistoryById
import com.snapcat.data.remote.response.ResponseLogin
import com.snapcat.data.remote.response.ResponsePrediction
import com.snapcat.data.remote.response.ResponseRegister
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

class CombinedApiServiceImpl(
    private val apiServiceCC: ApiServiceCC,
    private val apiServiceML: ApiServiceML,
) : CombinedApiService {

    override suspend fun register(requestBody: User): ResponseRegister  = apiServiceCC.register(requestBody)

    override suspend fun login(requestBody: User): ResponseLogin = apiServiceCC.login(requestBody)

    override suspend fun forgotPassword(email: User): Response<ResponseBody> = apiServiceCC.forgotPassword(email)
    override suspend fun saveToHistory(
        token: String,
        requestBody: History,
    ): Response<ResponseBody> = apiServiceCC.saveToHistory(token, requestBody)

    override suspend fun getUser(token: String, id: String): ResponseGetUser = apiServiceCC.getUser(token, id)

    override suspend fun getAllShop(token: String, id: String): ResponseGetAllShop = apiServiceCC.getAllShop(token, id)

    override suspend fun getAllHistories(token: String, id: String): ResponseGetAllHistories = apiServiceCC.getAllHistories(token, id)

    override suspend fun getHistoryById(token: String, id: String): ResponseHistoryById = apiServiceCC.getHistoryById(token, id)

    override suspend fun prediction(image: MultipartBody.Part): ResponsePrediction = apiServiceML.prediction(image)

}
