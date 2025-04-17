package com.vangelnum.wishes.features.userwishsendinghistory.data.api

import com.vangelnum.wishes.features.home.getwish.data.model.Wish
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface UserWishesHistoryApi {
    @GET("/api/v1/wish/my")
    suspend fun getMyWishes(): List<Wish>

    @DELETE("/api/v1/wish/{id}")
    suspend fun deleteWish(@Path("id") id: Int): Response<Unit>
}