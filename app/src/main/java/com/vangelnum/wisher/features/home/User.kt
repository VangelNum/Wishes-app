package com.vangelnum.wisher.features.home

data class User(
    val avatarUrl: String?,
    val email: String,
    val id: Int,
    val name: String,
    val password: String,
    val role: String,
    val coins: Int,
    val verificationCode: String?,
    val isEmailVerified: Boolean
)