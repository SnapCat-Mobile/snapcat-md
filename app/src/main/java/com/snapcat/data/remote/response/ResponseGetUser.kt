package com.snapcat.data.remote.response

import com.google.gson.annotations.SerializedName

data class ResponseGetUser(

	@field:SerializedName("data")
	val dataUser: DataUser,

	@field:SerializedName("message")
	val message: String
)

data class DataUser(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("password")
	val password: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("url_profile")
	val urlProfile: String,

	@field:SerializedName("username")
	val username: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)
