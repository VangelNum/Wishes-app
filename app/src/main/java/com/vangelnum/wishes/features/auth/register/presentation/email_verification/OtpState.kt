package com.vangelnum.wishes.features.auth.register.presentation.email_verification

data class OtpState(
    val code: List<Int?> = (1..6).map { null },
    val focusedIndex: Int? = null,
    val isValid: Boolean? = false
)