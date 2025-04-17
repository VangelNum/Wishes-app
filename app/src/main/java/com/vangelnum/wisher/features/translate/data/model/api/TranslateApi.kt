package com.vangelnum.wisher.features.translate.data.model.api

import retrofit2.http.GET
import retrofit2.http.Query

interface TranslateApi {
    @GET("/api/v1/translate/")
    suspend fun translateText(
        @Query("text") text: String,
        @Query("langpair") langpair: String? = null
    ): String
}