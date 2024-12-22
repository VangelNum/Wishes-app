package com.vangelnum.wisher.presentation

data class RegistrationState(
    val name: String = "",
    val nameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val avatarUrl: String? = null,
    val isLoading: Boolean = false
)