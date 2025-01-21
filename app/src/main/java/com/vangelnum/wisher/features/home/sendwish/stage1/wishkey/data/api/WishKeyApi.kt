package com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.data.api

import com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.data.model.WishKey
import retrofit2.http.GET
import retrofit2.http.POST

interface WishKeyApi {
    @GET("/api/v1/wish-key/my")
    suspend fun getWishKey(): WishKey?

    @POST("/api/v1/wish-key/generate")
    suspend fun generateWishKey(): WishKey

    @POST("/api/v1/wish-key/regenerate")
    suspend fun regenerateWishKey(): WishKey
}