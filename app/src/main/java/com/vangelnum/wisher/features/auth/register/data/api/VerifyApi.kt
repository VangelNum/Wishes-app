package com.vangelnum.wisher.features.auth.register.data.api

import com.vangelnum.wisher.features.auth.core.model.AuthResponse
import com.vangelnum.wisher.features.auth.register.data.model.EmailVerifyRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface VerifyApi {
    @POST("/api/v1/user/verify-email")
    suspend fun verifyEmail(@Body request: EmailVerifyRequest): AuthResponse
}