package com.vangelnum.wishes.features.profile.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.core.utils.ErrorParser
import com.vangelnum.wishes.features.auth.core.model.AuthResponse
import com.vangelnum.wishes.features.home.sendwish.createwish.data.api.UploadImageApi
import com.vangelnum.wishes.features.profile.data.api.ProfileApi
import com.vangelnum.wishes.features.profile.data.model.UpdateProfileRequest
import com.vangelnum.wishes.features.profile.domain.repository.UpdateProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
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
        avatar: String?
    ): Flow<UiState<AuthResponse>> = flow {
        try {
            val updateProfileRequest = UpdateProfileRequest(
                name = name,
                email = email,
                avatarUrl = avatar,
                newPassword = password,
                currentPassword = currentPassword
            )
            val response = profileApi.updateProfileInfo(updateProfileRequest)
            emit(UiState.Success(response))
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.parseError(e)))
        }
    }

    override fun uploadProfileImage(imageUri: Uri, context: Context): Flow<UiState<String>> = flow {
        emit(UiState.Loading())
        Log.d("UploadDebug", "Starting uploadProfileImage for URI: $imageUri")
        val seed = UUID.randomUUID()
        val inputStream: InputStream? = try {
            context.contentResolver.openInputStream(imageUri)
        } catch (e: Exception) {
            Log.e("UploadDebug", "Error opening InputStream: ${e.message}", e)
            null
        }

        if (inputStream == null) {
            Log.e("UploadDebug", "InputStream is null for URI: $imageUri")
            emit(UiState.Error("Failed to read image data from Uri (InputStream was null)"))
        }

        Log.d("UploadDebug", "InputStream opened successfully.")

        inputStream.use { stream ->
            try {
                Log.d("UploadDebug", "Decoding stream...")
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
                Log.d("UploadDebug", "Calling updateImageApi.uploadImage...")
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