package com.vangelnum.wisher.features.auth.presentation

import com.vangelnum.wisher.features.auth.data.model.RegistrationRequest

sealed class RegistrationEvent {
    data class OnRegisterUser(val user: RegistrationRequest): RegistrationEvent()
    data object OnBackToEmptyState: RegistrationEvent()
}