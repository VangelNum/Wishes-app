package com.vangelnum.wisher.features.home.sendwish.stage3.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GenerationTextApi {
    @GET("api/v1/generate/text/{prompt}")
    suspend fun generateText(
        @Path("prompt") prompt: String,
        @Query("model") model: String?,
        @Query("json") json: Boolean? = false
    ): String
}
