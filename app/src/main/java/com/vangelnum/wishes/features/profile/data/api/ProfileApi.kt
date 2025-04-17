package com.vangelnum.wishes.features.profile.data.api

import com.vangelnum.wishes.features.auth.core.model.AuthResponse
import com.vangelnum.wishes.features.profile.data.model.UpdateProfileRequest
import retrofit2.http.Body
import retrofit2.http.PUT

interface ProfileApi {
    @PUT("/api/v1/user")
    suspend fun updateProfileInfo(@Body updateProfileRequest: UpdateProfileRequest): AuthResponse
}