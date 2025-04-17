package com.vangelnum.wishes.features.keylogshistory.data.api

import com.vangelnum.wishes.features.keylogshistory.data.model.KeyLogsHistory
import retrofit2.http.GET

interface KeyLogsHistoryApi {
    @GET("/api/v1/wish-key/key-view-logs/my")
    suspend fun getKeyLogsHistory(): List<KeyLogsHistory>
}