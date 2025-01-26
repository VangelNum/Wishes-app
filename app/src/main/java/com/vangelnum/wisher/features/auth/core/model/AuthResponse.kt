package com.vangelnum.wisher.features.auth.core.model

data class AuthResponse(
    val id: Int,
    val name: String,
    val password: String,
    val email: String,
    val avatarUrl: String?,
    val role: String,
    val coins: Int,
    val verificationCode: String,
    val isEmailVerified: Boolean
)