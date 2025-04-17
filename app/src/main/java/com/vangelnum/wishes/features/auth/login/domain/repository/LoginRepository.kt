package com.vangelnum.wishes.features.auth.login.domain.repository

import com.vangelnum.wishes.features.auth.core.model.AuthResponse
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    fun loginUser(authorizationHeader: String): Flow<AuthResponse>
    fun refreshUserData(authorizationHeader: String): Flow<AuthResponse>
}