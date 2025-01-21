package com.vangelnum.wisher.features.auth.presentation.registration.stage2.data.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.auth.presentation.registration.stage2.domain.repository.UploadImageRepository
import com.vangelnum.wisher.features.home.sendwish.stage3.data.api.UploadImageApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.HttpException
import javax.inject.Inject

class UploadImageRepositoryImpl @Inject constructor(
    private val apiService: UploadImageApi
) : UploadImageRepository {
    override fun uploadImage(imagePart: MultipartBody.Part): Flow<UiState<String>> = flow {
        emit(UiState.Loading())
        try {
            val response = apiService.uploadImage(imagePart)
            emit(UiState.Success(response))
        } catch (e: HttpException) {
            emit(UiState.Error(e.message() ?: "HTTP Error"))
        } catch (e: Exception) {
            emit(UiState.Error("An unexpected error occurred"))
        }
    }
}