package com.snapcat.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ResponsePrediction(
	val data: DataPrediction,
	val status: StatusPrediction
)

data class StatusPrediction(
	val code: Int,
	val message: String
)


@Parcelize
data class DataPrediction(
	@SerializedName("cat_breed_predictions")
	val catBreedPredictions: String = "",
	@SerializedName("upload_image")
	val uploadImage: String = "",
	@SerializedName("cat_breed_description")
	val catBreedDescription: String = "",
	@SerializedName("confidence")
	val confidence: Double = 0.0
): Parcelable

