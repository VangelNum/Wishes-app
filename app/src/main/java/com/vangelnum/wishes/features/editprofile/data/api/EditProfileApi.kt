package com.vangelnum.wishes.features.editprofile.data.api

import com.vangelnum.wishes.features.auth.core.model.AuthResponse
import com.vangelnum.wishes.features.editprofile.data.model.EditProfileRequest
import retrofit2.http.Body
import retrofit2.http.PUT

interface EditProfileApi {
    @PUT("/api/v1/user")
    suspend fun editProfile(
        @Body editProfileRequest: EditProfileRequest
    ): AuthResponse
}