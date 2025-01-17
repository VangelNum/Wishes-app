package com.vangelnum.wisher.features.auth.presentation.login

sealed class LoginEvent {
    data class OnLoginUser(val email: String, val password: String): LoginEvent()
    data object OnBackToEmptyState: LoginEvent()
    data object OnExit: LoginEvent()
    data object OnEnterApp: LoginEvent()
}