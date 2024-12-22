package com.vangelnum.wisher.data.model

import com.google.gson.annotations.SerializedName

data class UserRequest(
    val name: String,
    val password: String,
    val email: String,
    @SerializedName("avatarUrl")
    val avatarUrl: String? = null
)