package com.vangelnum.wisher.features.home.sendwish.stage3.data.api

import com.vangelnum.wisher.features.home.sendwish.stage3.data.model.SendWishRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface SendWishApi {
    @POST("/api/v1/wish")
    suspend fun sendWish(
        @Body request: SendWishRequest
    )
}