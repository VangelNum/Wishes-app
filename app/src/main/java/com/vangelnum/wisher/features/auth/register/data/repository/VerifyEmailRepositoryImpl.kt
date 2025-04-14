package com.vangelnum.wisher.features.auth.register.data.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.utils.ErrorParser
import com.vangelnum.wisher.features.auth.core.model.AuthResponse
import com.vangelnum.wisher.features.auth.register.data.api.VerifyApi
import com.vangelnum.wisher.features.auth.register.data.model.EmailVerifyRequest
import com.vangelnum.wisher.features.auth.register.data.model.ResendVerificationCodeRequest
import com.vangelnum.wisher.features.auth.register.domain.repository.VerifyEmailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VerifyEmailRepositoryImpl @Inject constructor(
    private val verifyApi: VerifyApi,
    private val errorParser: ErrorParser
) : VerifyEmailRepository {
    override fun verifyEmail(email: String, verificationCode: String): Flow<UiState<AuthResponse>> =
        flow {
            emit(UiState.Loading())
            try {
                val response = verifyApi.verifyEmail(EmailVerifyRequest(email, verificationCode))
                emit(UiState.Success(response))
            } catch (e: Exception) {
                emit(UiState.Error(errorParser.parseError(e)))
            }
        }

    override suspend fun resendVerificationCode(email: String): String {
        val request = ResendVerificationCodeRequest(email = email)
        return verifyApi.resendVerificationCode(request)
    }
}