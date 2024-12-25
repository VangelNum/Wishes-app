package com.vangelnum.wisher.features.auth.presentation

sealed class LoginEvent {
    data class onLoginUser(val email: String, val password: String): LoginEvent()
    object onBackToEmptyState: LoginEvent()
    object onExit: LoginEvent()
    object onEnterApp: LoginEvent()
}