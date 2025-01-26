package com.vangelnum.wisher.features.auth.login.domain.repository

import com.vangelnum.wisher.features.auth.core.model.AuthResponse
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    fun loginUser(authorizationHeader: String): Flow<AuthResponse>
}