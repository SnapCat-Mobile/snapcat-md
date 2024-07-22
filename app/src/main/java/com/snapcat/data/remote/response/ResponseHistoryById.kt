package com.snapcat.data.remote.response

import com.google.gson.annotations.SerializedName

data class ResponseHistoryById(

	@field:SerializedName("data")
	val dataHistoryId: DataHistoryId,

	@field:SerializedName("message")
	val message: String
)

data class DataHistoryId(

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("userId")
	val userId: String,

	@field:SerializedName("breed")
	val breed: String
)
