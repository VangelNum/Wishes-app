package com.vangelnum.wisher.features.home.sendwish.stage3.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GenerateImageApi {
    @GET("/prompt/{prompt}")
    suspend fun generateImage(
        @Path("prompt") prompt: String,
        @Query("model") model: String,
        @Query("seed") seed: Int,
        @Query("width") width: Int,
        @Query("height") height: Int,
        @Query("nologo") nologo: Boolean
    )

    @GET("/models")
    suspend fun getListOfModels(): List<String>
}