package com.vangelnum.wisher.presentation

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)