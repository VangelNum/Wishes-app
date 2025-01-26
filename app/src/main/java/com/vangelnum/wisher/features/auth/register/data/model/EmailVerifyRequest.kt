package com.vangelnum.wisher.features.auth.register.data.model

data class EmailVerifyRequest(
    val email: String,
    val verificationCode: String
)