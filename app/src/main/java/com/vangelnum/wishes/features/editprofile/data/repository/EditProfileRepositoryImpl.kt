package com.vangelnum.wishes.features.editprofile.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.core.utils.ErrorParser
import com.vangelnum.wishes.features.auth.core.model.AuthResponse
import com.vangelnum.wishes.features.editprofile.data.api.EditProfileApi
import com.vangelnum.wishes.features.editprofile.data.model.EditProfileRequest
import com.vangelnum.wishes.features.editprofile.domain.repository.EditProfileRepository
import com.vangelnum.wishes.features.home.sendwish.createwish.data.api.UploadImageApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject

class EditProfileRepositoryImpl @Inject constructor(
    private val editProfileApi: EditProfileApi,
    private val updateImageApi: UploadImageApi,
    private val errorParser: ErrorParser,
) : EditProfileRepository {
    override fun updateUserProfile(
        name: String?,
        email: String?,
        avatarUrl: String?,
        newPassword: String?,
        currentPassword: String?
    ): Flow<UiState<AuthResponse>> = flow {
        emit(UiState.Loading())
        try {
            val editProfileRequest = EditProfileRequest(
                name = name,
                email = email,
                avatarUrl = avatarUrl,
                newPassword = newPassword,
                currentPassword = currentPassword
            )
            val response = editProfileApi.editProfile(editProfileRequest)
            emit(UiState.Success(response))
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.parseError(e)))
        }
    }

    override fun uploadProfileImage(imageUri: Uri, context: Context): Flow<UiState<String>> = flow {
        emit(UiState.Loading())
        val seed = UUID.randomUUID()
        val inputStream: InputStream? = try {
            context.contentResolver.openInputStream(imageUri)
        } catch (e: Exception) {
            null
        }

        if (inputStream == null) {
            emit(UiState.Error("Failed to read image data from Uri"))
        }

        Log.d("UploadDebug", "InputStream opened successfully.")

        inputStream.use { stream ->
            try {
                val originalBitmap = BitmapFactory.decodeStream(stream)
                if (originalBitmap == null) {
                    Log.e("UploadDebug", "BitmapFactory.decodeStream returned null.")
                    throw Exception("Failed to decode bitmap stream")
                }
                Log.d("UploadDebug", "Bitmap decoded successfully. Compressing...")
                val outputStream = ByteArrayOutputStream()

                originalBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
                val byteArray = outputStream.toByteArray()
                Log.d("UploadDebug", "Bitmap compressed. Size: ${byteArray.size}. Creating request...")

                val requestFile = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData(
                    "image",
                    "avatar_$seed.jpg",
                    requestFile
                )
                val result = updateImageApi.uploadImage(imagePart)
                Log.d("UploadDebug", "API call successful. Result: $result")
                emit(UiState.Success(result))
            } catch (e: Exception) {
                Log.e("UploadDebug", "Exception inside 'use' block: ${e.message}", e)
                emit(UiState.Error("Exception inside 'use' block: ${e.message}"))
            }
        }
    }
}