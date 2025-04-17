package com.vangelnum.wishes.features.auth.register.domain.repository

import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.auth.core.model.AuthResponse
import kotlinx.coroutines.flow.Flow

interface VerifyEmailRepository {
    fun verifyEmail(email: String, verificationCode: String): Flow<UiState<AuthResponse>>
    suspend fun resendVerificationCode(email: String): String
}