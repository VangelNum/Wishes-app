package com.vangelnum.wishes.features.auth.register.data.model

data class EmailVerifyRequest(
    val email: String,
    val verificationCode: String
)