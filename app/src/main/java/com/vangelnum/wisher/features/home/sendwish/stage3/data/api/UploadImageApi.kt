package com.vangelnum.wisher.features.home.sendwish.stage3.data.api

import com.vangelnum.wisher.features.home.sendwish.stage3.data.model.ImgbbResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UploadImageApi {
    @Multipart
    @POST("/1/upload")
    suspend fun uploadImage(
        @Query("key") key: String,
        @Part image: MultipartBody.Part
    ): ImgbbResponse
}