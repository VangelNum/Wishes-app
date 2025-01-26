package com.vangelnum.wisher.features.userwisheshistory.data.api

import com.vangelnum.wisher.features.home.getwish.data.model.Wish
import retrofit2.http.GET

interface UserWishesHistoryApi {
    @GET("/api/v1/wish/my")
    suspend fun getMyWishes(): List<Wish>
}