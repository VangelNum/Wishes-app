package com.vangelnum.wisher.features.home.getwish.data.api

import com.vangelnum.wisher.features.home.getwish.data.model.GetWishResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface GetWishApi {
    @GET("/api/v1/wish/{key}")
    suspend fun getWishesByKey(@Path("key") key: String): List<GetWishResponse>
}