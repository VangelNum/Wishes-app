package com.vangelnum.wisher.features.auth.presentation.registration.stage2.domain.repository

import com.vangelnum.wisher.features.auth.presentation.registration.stage2.data.model.UploadAvatarResponse
import okhttp3.MultipartBody

interface UploadAvatarRepository {
    suspend fun uploadImage (imagePart: MultipartBody.Part): Result<UploadAvatarResponse>
}