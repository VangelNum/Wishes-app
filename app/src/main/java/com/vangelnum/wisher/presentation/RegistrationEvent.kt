package com.vangelnum.wisher.presentation

sealed class RegistrationEvent {
    data class NameChanged(val name: String) : RegistrationEvent()
    data class EmailChanged(val email: String) : RegistrationEvent()
    data class PasswordChanged(val password: String) : RegistrationEvent()
    data class AvatarUrlChanged(val avatarUrl: String) : RegistrationEvent()
}