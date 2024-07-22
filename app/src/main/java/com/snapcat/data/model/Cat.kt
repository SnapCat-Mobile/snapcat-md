package com.snapcat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "cat")
data class Cat(
    @field:PrimaryKey
    @field:SerializedName("id")
    val id: String = "",
)