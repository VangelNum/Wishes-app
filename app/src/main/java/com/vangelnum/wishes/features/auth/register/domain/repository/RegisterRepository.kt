package com.vangelnum.wishes.features.auth.register.domain.repository

import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.auth.register.data.model.RegistrationRequest
import kotlinx.coroutines.flow.Flow

interface RegisterRepository {
    fun register(registrationRequest: RegistrationRequest): Flow<UiState<String>>
}