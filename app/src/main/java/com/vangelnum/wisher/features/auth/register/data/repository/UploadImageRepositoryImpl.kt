package com.vangelnum.wisher.features.auth.register.data.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.utils.ErrorParser
import com.vangelnum.wisher.features.auth.core.model.AuthResponse
import com.vangelnum.wisher.features.auth.register.data.api.RegisterApi
import com.vangelnum.wisher.features.auth.register.data.model.UpdateAvatarRequest
import com.vangelnum.wisher.features.auth.register.domain.repository.UploadImageRepository
import com.vangelnum.wisher.features.home.sendwish.createwish.data.api.UploadImageApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

class UploadImageRepositoryImpl @Inject constructor(
    private val apiService: UploadImageApi,
    private val registerApi: RegisterApi,
    private val errorParser: ErrorParser
) : UploadImageRepository {
    override fun uploadImage(imagePart: MultipartBody.Part): Flow<UiState<String>> = flow {
        emit(UiState.Loading())
        try {
            val response = apiService.uploadImage(imagePart)
            emit(UiState.Success(response))
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.parseError(e)))
        }
    }

    override fun updateAvatar(avatarUrl: String): Flow<AuthResponse> = flow {
        val response = registerApi.updateAvatar(UpdateAvatarRequest(avatarUrl))
        emit(response)
    }
}