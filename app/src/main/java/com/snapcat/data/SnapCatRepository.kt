package com.snapcat.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.snapcat.data.local.database.SnapCatDao
import com.snapcat.data.model.History
import com.snapcat.data.model.User
import com.snapcat.data.remote.response.ResponseGetAllHistories
import com.snapcat.data.remote.response.ResponseGetAllShop
import com.snapcat.data.remote.response.ResponseGetUser
import com.snapcat.data.remote.response.ResponseHistoryById
import com.snapcat.data.remote.retrofit.ApiService
import com.snapcat.data.remote.retrofit.ApiServiceCC
import com.snapcat.data.remote.retrofit.ApiServiceML
import com.snapcat.data.remote.retrofit.CombinedApiService
import com.snapcat.data.remote.retrofit.CombinedApiServiceImpl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject

class SnapCatRepository (
    private val apiService: CombinedApiService,
) {

    fun register(user: User) = liveData {
        try {
            emit(ResultMessage.Loading)
            val response = apiService.register(user)
            emit(ResultMessage.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(ResultMessage.Error(Exception(errorBody ?: "Unknown error")))
        } catch (e: IOException) {
            emit(ResultMessage.Error(Exception("No network connection")))
        }
    }

    fun login(user: User) = liveData {
        try {
            emit(ResultMessage.Loading)
            val response = apiService.login(user)
            emit(ResultMessage.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(ResultMessage.Error(Exception(errorBody ?: "Unknown error")))
        } catch (e: IOException) {
            emit(ResultMessage.Error(Exception("No network connection")))
        }
    }

    fun forgotPassword(email: User) = liveData {
        try {
            emit(ResultMessage.Loading)
            val response = apiService.forgotPassword(email)
            emit(ResultMessage.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(ResultMessage.Error(Exception(errorBody ?: "Unknown error")))
        } catch (e: IOException) {
            emit(ResultMessage.Error(Exception("No network connection")))
        }
    }

    fun getAllShop(token: String, id: String) = liveData {
        try {
            emit(ResultMessage.Loading)
            val response = apiService.getAllShop("Bearer $token", id)
            emit(ResultMessage.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            emit(ResultMessage.Error(e))
        } catch (e: IOException) {
            emit(ResultMessage.Error(Exception("No network connection")))
        }
    }

    fun getAllHistories(token: String, id: String): LiveData<ResultMessage<ResponseGetAllHistories>> = liveData {
        try {
            emit(ResultMessage.Loading)
            val response = apiService.getAllHistories("Bearer $token", id)
            emit(ResultMessage.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            emit(ResultMessage.Error(e))
        } catch (e: IOException) {
            emit(ResultMessage.Error(Exception("No network connection")))
        }
    }

    fun getUser(token: String, id: String): LiveData<ResultMessage<ResponseGetUser>> = liveData {
        try {
            emit(ResultMessage.Loading)
            val response = apiService.getUser("Bearer $token", id)
            emit(ResultMessage.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            emit(ResultMessage.Error(e))
        } catch (e: IOException) {
            emit(ResultMessage.Error(Exception("No network connection")))
        }
    }

    fun getHistoryById(token: String, id: String): LiveData<ResultMessage<ResponseHistoryById>> = liveData {
        try {
            emit(ResultMessage.Loading)
            val response = apiService.getHistoryById("Bearer $token", id)
            emit(ResultMessage.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            emit(ResultMessage.Error(e))
        } catch (e: IOException) {
            emit(ResultMessage.Error(Exception("No network connection")))
        }
    }

//    fun historyById(id: String) = liveData {
//        try {
//            emit(ResultMessage.Loading)
//            val response = apiService.historyById(id)
//            emit(ResultMessage.Success(response))
//        } catch (e: HttpException) {
//            val jsonInString = e.response()?.errorBody()?.string()
//            emit(ResultMessage.Error(e))
//        } catch (e: IOException) {
//            emit(ResultMessage.Error(Exception("No network connection")))
//        }
//    }
//
//
//    fun shopById(id: String) = liveData {
//        try {
//            emit(ResultMessage.Loading)
//            val response = apiService.shopById(id)
//            emit(ResultMessage.Success(response))
//        } catch (e: HttpException) {
//            val jsonInString = e.response()?.errorBody()?.string()
//            emit(ResultMessage.Error(e))
//        } catch (e: IOException) {
//            emit(ResultMessage.Error(Exception("No network connection")))
//        }
//    }

    fun prediction(image: File) = liveData {
        val requestImageFile = image.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "image",
            image.name,
            requestImageFile
        )
        try {
            emit(ResultMessage.Loading)
            val response = apiService.prediction(multipartBody)
            emit(ResultMessage.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(ResultMessage.Error(Exception(errorBody ?: "Unknown error")))
        } catch (e: IOException) {
            emit(ResultMessage.Error(Exception("No network connection")))
        }
    }

    fun saveToHistory(token: String, history: History) = liveData {
        try {
            emit(ResultMessage.Loading)
            val response = apiService.saveToHistory(token, history)
            emit(ResultMessage.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(ResultMessage.Error(Exception(errorBody ?: "Unknown error")))
        } catch (e: IOException) {
            emit(ResultMessage.Error(Exception("No network connection")))
        }
    }

    companion object {
        @Volatile
        private var instance: SnapCatRepository? = null
        fun getInstance(combinedApiService: CombinedApiService): SnapCatRepository =
            instance ?: synchronized(this) {
                instance ?: SnapCatRepository(combinedApiService)
            }.also { instance = it }
    }

}