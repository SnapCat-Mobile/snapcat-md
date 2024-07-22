package com.snapcat.data.remote.response

import com.google.gson.annotations.SerializedName

data class ResponseGetAllShop(

	@field:SerializedName("data")
	var data: List<Data>,

	@field:SerializedName("message")
	val message: String
)

data class Data(

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("url_shop")
	val urlShop: String
)
