package com.vangelnum.wisher.data.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val id: Int,
    val name: String,
    val password: String,
    val email: String,
    @SerializedName("avatarUrl")
    val avatarUrl: String?,
    val role: String,
    val maxWishes: Int
)