package com.vangelnum.wisher.features.auth.presentation.registration.stage2.data.repository

import com.vangelnum.wisher.features.auth.presentation.registration.stage2.data.api.UploadAvatarService
import com.vangelnum.wisher.features.auth.presentation.registration.stage2.data.model.UploadAvatarResponse
import com.vangelnum.wisher.features.auth.presentation.registration.stage2.domain.repository.UploadAvatarRepository
import okhttp3.MultipartBody

class UploadAvatarRepositoryImpl(
    private val apiService: UploadAvatarService
) : UploadAvatarRepository {
    override suspend fun uploadImage(imagePart: MultipartBody.Part): Result<UploadAvatarResponse> {
        return try {
            val apiKey = "f90248ad8f4b1e262a5e8e7603645cc1"
            val response = apiService.uploadImage(apiKey, imagePart)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}