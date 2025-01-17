package com.vangelnum.wisher.features.auth.presentation.registration.stage2.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.auth.data.model.AuthResponse
import com.vangelnum.wisher.features.auth.data.model.RegistrationRequest
import com.vangelnum.wisher.features.auth.domain.repository.UserRepository
import com.vangelnum.wisher.features.auth.presentation.registration.stage1.RegistrationEvent
import com.vangelnum.wisher.features.auth.presentation.registration.stage2.data.model.UploadAvatarResponse
import com.vangelnum.wisher.features.auth.presentation.registration.stage2.domain.repository.UploadAvatarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class UploadAvatarViewModel @Inject constructor(
    private val uploadAvatarRepository: UploadAvatarRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uploadAvatarState = MutableStateFlow<UiState<UploadAvatarResponse>>(UiState.Idle)
    val uploadAvatarState: StateFlow<UiState<UploadAvatarResponse>> = _uploadAvatarState.asStateFlow()

    private val _registrationState = MutableStateFlow<UiState<AuthResponse>>(UiState.Idle)
    val registrationState = _registrationState.asStateFlow()

    fun onEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.OnRegisterUser -> {
                registerUser(event.user)
            }

            RegistrationEvent.OnBackToEmptyState -> {
                _uploadAvatarState.value = UiState.Idle
                _registrationState.value = UiState.Idle
            }

            is RegistrationEvent.OnUploadAvatar -> {
                uploadImage(event.context, event.imageUri)
            }
        }
    }


    private fun uploadImage(context: Context, imageUri: Uri) {
        _uploadAvatarState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                inputStream?.let {
                    val byteArray = it.readBytes()
                    val requestFile = byteArray.toRequestBody("image/*".toMediaTypeOrNull())
                    val imagePart = MultipartBody.Part.createFormData(
                        "image",
                        "avatar.jpg",
                        requestFile
                    )
                    uploadAvatarRepository.uploadImage(imagePart).fold(
                        onSuccess = { response ->
                            _uploadAvatarState.value = UiState.Success(response)
                        },
                        onFailure = { error ->
                            _uploadAvatarState.value = UiState.Error(error.message ?: "Upload failed")
                        }
                    )
                } ?: run {
                    _uploadAvatarState.value = UiState.Error("Could not open image stream")
                }
            } catch (e: Exception) {
                _uploadAvatarState.value = UiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    private fun registerUser(user: RegistrationRequest) {
        viewModelScope.launch {
            _registrationState.value = UiState.Loading
            userRepository.register(user).fold(
                onSuccess = { registeredUser ->
                    _registrationState.value = UiState.Success(registeredUser)
                },
                onFailure = { error ->
                    _registrationState.value = UiState.Error(error.localizedMessage ?: "An unexpected error occurred")
                }
            )
        }
    }
}