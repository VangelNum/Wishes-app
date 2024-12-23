package com.vangelnum.wisher.features.auth.api

import com.vangelnum.wisher.features.auth.data.model.AuthResponse
import com.vangelnum.wisher.features.auth.data.model.RegistrationRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/v1/user/register")
    suspend fun register(@Body request: RegistrationRequest): AuthResponse

    @GET("/api/v1/user/me")
    suspend fun getUserInfo(): AuthResponse
}