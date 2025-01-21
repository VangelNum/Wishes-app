package com.vangelnum.wisher.features.auth.domain.repository

import com.vangelnum.wisher.features.auth.data.model.AuthResponse
import com.vangelnum.wisher.features.auth.data.model.RegistrationRequest
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun register(registrationRequest: RegistrationRequest): Flow<AuthResponse>
    fun getUserInfo(authorizationHeader: String): Flow<AuthResponse>
}