package com.vangelnum.wishes.features.auth.login.data.api

import com.vangelnum.wishes.features.auth.core.model.AuthResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface LoginApi {
    @GET("/api/v1/user/me")
    suspend fun getUserInfo(
        @Header("Authorization") authorization: String
    ): AuthResponse
}