package com.vangelnum.wisher.features.home.sendwish.stage2.data.api

import com.vangelnum.wisher.features.home.sendwish.stage2.data.model.Holiday
import retrofit2.http.GET
import retrofit2.http.Query

interface HolidayApi {
    @GET("/api/v1/holidays")
    suspend fun getHolidays(@Query("date") date: String): List<Holiday>
}