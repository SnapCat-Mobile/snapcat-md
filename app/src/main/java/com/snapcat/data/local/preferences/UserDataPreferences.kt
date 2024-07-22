package com.snapcat.data.local.preferences

data class UserDataPreferences(
    val userId: String,
    val username: String,
    val email: String,
    val token: String
)