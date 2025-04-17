package com.vangelnum.wishes.features.auth.register.data.api

import com.vangelnum.wishes.features.auth.core.model.AuthResponse
import com.vangelnum.wishes.features.auth.register.data.model.RegistrationRequest
import com.vangelnum.wishes.features.auth.register.data.model.UpdateAvatarRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface RegisterApi {
    @POST("/api/v1/user/register")
    suspend fun register(@Body request: RegistrationRequest): String

    @PUT("/api/v1/user/avatar")
    suspend fun updateAvatar(
        @Body request: UpdateAvatarRequest
    ): AuthResponse
}