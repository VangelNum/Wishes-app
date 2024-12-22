package com.vangelnum.wisher.data.api

import com.vangelnum.wisher.data.model.UserRequest
import com.vangelnum.wisher.data.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {

    @POST("/api/v1/user/register")
    suspend fun register(@Body request: UserRequest): UserResponse

    @GET("/api/v1/user/me")
    suspend fun getMe(): UserResponse
}