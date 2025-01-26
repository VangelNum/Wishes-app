package com.vangelnum.wisher.features.auth.login.presentation

sealed class LoginEvent {
    data class OnLoginUser(val email: String, val password: String) : LoginEvent()
    data object OnBackToEmptyState : LoginEvent()
    data object OnExit : LoginEvent()
    data object OnEnterApp : LoginEvent()
}