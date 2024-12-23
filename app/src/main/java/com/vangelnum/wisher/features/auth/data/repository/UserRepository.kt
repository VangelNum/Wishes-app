package com.vangelnum.wisher.features.auth.data.repository

import com.vangelnum.wisher.features.auth.data.model.AuthResponse
import com.vangelnum.wisher.features.auth.data.model.RegistrationRequest

interface UserRepository {
    suspend fun register(registrationRequest: RegistrationRequest): Result<AuthResponse>
    suspend fun getUserInfo(email: String, password: String): Result<AuthResponse>
}