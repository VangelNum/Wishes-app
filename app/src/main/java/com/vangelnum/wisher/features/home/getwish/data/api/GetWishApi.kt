package com.vangelnum.wisher.features.home.getwish.data.api

import com.vangelnum.wisher.features.home.getwish.data.model.WishDatesInfo
import com.vangelnum.wisher.features.home.getwish.data.model.WishResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface GetWishApi {
    @GET("/api/v1/wish/{key}")
    suspend fun getWishesDatesByKey(@Path("key") key: String): List<WishDatesInfo>

    @GET("/api/v1/wish/{key}/{wishId}")
    suspend fun getWishes(@Path("key") key: String, @Path("wishId") wishId: Int): WishResponse
}