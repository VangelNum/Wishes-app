package com.vangelnum.wisher.features.profile.data.api

import com.vangelnum.wisher.features.auth.core.model.AuthResponse
import com.vangelnum.wisher.features.profile.data.model.UpdateProfileRequest
import retrofit2.http.Body
import retrofit2.http.PUT

interface ProfileApi {
    @PUT("/api/v1/user")
    suspend fun updateProfileInfo(@Body updateProfileRequest: UpdateProfileRequest): AuthResponse
}