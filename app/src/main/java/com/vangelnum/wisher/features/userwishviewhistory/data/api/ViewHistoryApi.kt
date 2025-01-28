package com.vangelnum.wisher.features.userwishviewhistory.data.api

import com.vangelnum.wisher.features.userwishviewhistory.data.model.ViewHistory
import retrofit2.http.GET
import retrofit2.http.Path

interface ViewHistoryApi {
    @GET("/api/v1/wish/{wishId}/view-logs")
    suspend fun getViewHistory(
        @Path("wishId") wishId: Int
    ): List<ViewHistory>
}