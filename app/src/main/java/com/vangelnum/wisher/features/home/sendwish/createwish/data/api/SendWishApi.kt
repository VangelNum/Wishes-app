package com.vangelnum.wisher.features.home.sendwish.createwish.data.api

import com.vangelnum.wisher.features.home.getwish.data.model.Wish
import com.vangelnum.wisher.features.home.sendwish.createwish.data.model.SendWishRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SendWishApi {
    @POST("/api/v1/wish")
    suspend fun sendWish(
        @Body request: SendWishRequest
    ): Wish

    @GET("/api/v1/wish/my/count")
    suspend fun getNumberWishesOfCurrentUser(): Long
}