package com.vangelnum.wisher.features.auth.presentation.registration.stage2.domain.repository

import com.vangelnum.wisher.core.data.UiState
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface UploadImageRepository {
    fun uploadImage(imagePart: MultipartBody.Part): Flow<UiState<String>>
}