package com.vangelnum.wisher.features.auth.presentation.registration.stage2.data.api

import com.vangelnum.wisher.features.auth.presentation.registration.stage2.data.model.UploadAvatarResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UploadAvatarService {
    @Multipart
    @POST("1/upload")
    suspend fun uploadImage(
        @Query("key") apiKey: String,
        @Part image: MultipartBody.Part
    ): UploadAvatarResponse
}