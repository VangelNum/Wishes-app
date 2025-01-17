package com.vangelnum.wisher.features.home.sendwish.stage1.worldtime.data.api

import com.vangelnum.wisher.features.home.sendwish.stage1.worldtime.data.model.DateInfo
import retrofit2.http.GET
import retrofit2.http.Query

interface WorldTimeApi {
    @GET("now")
    suspend fun getCurrentDate(@Query("tz") timezone: String = "Europe/Moscow"): DateInfo
}