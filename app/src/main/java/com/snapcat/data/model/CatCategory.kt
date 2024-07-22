package com.snapcat.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CatCategory(
    val name: String,
    val photo: Int
): Parcelable