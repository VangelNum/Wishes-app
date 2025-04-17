package com.vangelnum.wishes.features.auth.register.data.api

import com.vangelnum.wishes.features.auth.core.model.AuthResponse
import com.vangelnum.wishes.features.auth.register.data.model.EmailVerifyRequest
import com.vangelnum.wishes.features.auth.register.data.model.ResendVerificationCodeRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface VerifyApi {
    @POST("/api/v1/user/verify-email")
    suspend fun verifyEmail(@Body request: EmailVerifyRequest): AuthResponse

    @POST("/api/v1/user/resend-verification-code")
    suspend fun resendVerificationCode(@Body request: ResendVerificationCodeRequest): String
}