package com.vangelnum.wisher.features.auth.data.model

data class RegistrationRequest(
    val name: String,
    val email: String,
    val password: String,
    val avatarUrl: String? = null
)