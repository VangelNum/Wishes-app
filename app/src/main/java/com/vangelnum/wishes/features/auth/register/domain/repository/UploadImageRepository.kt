package com.vangelnum.wishes.features.auth.register.domain.repository

import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.auth.core.model.AuthResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface UploadImageRepository {
    fun uploadImage(imagePart: MultipartBody.Part): Flow<UiState<String>>
    fun updateAvatar(avatarUrl: String): Flow<AuthResponse>
}