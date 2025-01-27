package com.vangelnum.wisher.features.profile.data.repository

import android.content.Context
import android.net.Uri
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.utils.ErrorParser
import com.vangelnum.wisher.features.auth.core.model.AuthResponse
import com.vangelnum.wisher.features.home.sendwish.stage3.data.api.UploadImageApi
import com.vangelnum.wisher.features.profile.data.api.ProfileApi
import com.vangelnum.wisher.features.profile.data.model.UpdateProfileRequest
import com.vangelnum.wisher.features.profile.domain.repository.UpdateProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject

class UpdateProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi,
    private val updateImageApi: UploadImageApi,
    private val errorParser: ErrorParser,
) : UpdateProfileRepository {
    override fun updateUserProfile(
        name: String?,
        email: String?,
        password: String?,
        currentPassword: String?,
        avatar: Uri?,
        context: Context
    ): Flow<UiState<AuthResponse>> = flow {
        try {
            val avatarUrl = avatar?.let { uploadProfileImage(it, context) }
            val updateProfileRequest = UpdateProfileRequest(
                name = name,
                email = email,
                avatarUrl = avatarUrl,
                newPassword = password,
                currentPassword = currentPassword
            )
            val response = profileApi.updateProfileInfo(updateProfileRequest)
            emit(UiState.Success(response))
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.parseError(e)))
        }
    }

    private suspend fun uploadProfileImage(imageUri: Uri, context: Context): String {
        val seed = UUID.randomUUID()
        val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
        inputStream?.use { stream ->
            val byteArray = stream.readBytes()
            val requestFile = byteArray.toRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData(
                "image",
                "avatar_$seed.jpg",
                requestFile
            )
            updateImageApi.uploadImage(imagePart)
        }
        throw Exception("Failed to read image data from Uri")
    }
}