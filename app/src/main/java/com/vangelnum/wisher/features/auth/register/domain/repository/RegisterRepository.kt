package com.vangelnum.wisher.features.auth.register.domain.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.auth.register.data.model.RegistrationRequest
import kotlinx.coroutines.flow.Flow

interface RegisterRepository {
    fun register(registrationRequest: RegistrationRequest): Flow<UiState<String>>
}