package com.vangelnum.wishes.features.home.sendwish.selectdate.worldtime.data.api

import com.vangelnum.wishes.features.home.sendwish.selectdate.worldtime.data.model.DateInfo
import retrofit2.http.GET
import retrofit2.http.Query

interface WorldTimeApi {
    @GET("/api/v1/world-time/now")
    suspend fun getCurrentDate(@Query("tz") timezone: String = "UTC"): DateInfo
}