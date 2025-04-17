package com.vangelnum.wishes.features.home.sendwish.createwish.data.api

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadImageApi {
    @Multipart
    @POST("/api/v1/upload/image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): String
}