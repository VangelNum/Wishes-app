package com.vangelnum.wishes.features.home.sendwish.createwish.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GenerationImageApi {
    @GET("/api/v1/generate/image/{prompt}")
    suspend fun generateImage(
        @Path("prompt") prompt: String,
        @Query("model") model: String,
        @Query("seed") seed: Int,
        @Query("width") width: Int,
        @Query("height") height: Int,
        @Query("nologo") nologo: Boolean,
        @Query("safe") safe: Boolean
    )

    @GET("/api/v1/generate/image/models")
    suspend fun getListOfModels(): List<String>
}