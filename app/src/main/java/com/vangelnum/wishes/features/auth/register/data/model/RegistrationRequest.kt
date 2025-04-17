package com.vangelnum.wishes.features.auth.register.data.model

data class RegistrationRequest(
    val name: String,
    val email: String,
    val password: String
)