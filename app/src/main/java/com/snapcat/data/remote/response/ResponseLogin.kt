package com.snapcat.data.remote.response

import com.google.gson.annotations.SerializedName
import com.snapcat.data.model.User

data class ResponseLogin(

	@field:SerializedName("data")
	val data: DataLogin,

	@field:SerializedName("message")
	val message: String
)

data class DataLogin(

	@field:SerializedName("accessToken")
	val accessToken: String,

	@field:SerializedName("user")
	val user: User,

	@field:SerializedName("refreshToken")
	val refreshToken: String
)
