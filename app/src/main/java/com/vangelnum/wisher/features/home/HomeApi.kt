package com.vangelnum.wisher.features.home

import retrofit2.http.GET

interface HomeApi {
    @GET("/api/v1/wish-key/my")
    suspend fun getWishKey(): WishKey
}