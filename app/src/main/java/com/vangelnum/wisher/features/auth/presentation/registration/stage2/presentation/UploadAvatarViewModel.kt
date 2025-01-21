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
import com.vangelnum.wisher.features.auth.presentation.registration.stage2.domain.repository.UploadImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class UploadAvatarViewModel @Inject constructor(
    private val uploadImageRepository: UploadImageRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uploadAvatarUiState = MutableStateFlow<UiState<String>>(UiState.Idle())
    val uploadAvatarUiState: StateFlow<UiState<String>> = _uploadAvatarUiState.asStateFlow()

    private val _registrationUiState = MutableStateFlow<UiState<AuthResponse>>(UiState.Idle())
    val registrationUiState = _registrationUiState.asStateFlow()

    fun onEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.OnRegisterUser -> registerUser(event.user)
            RegistrationEvent.OnBackToEmptyState -> resetState()
            is RegistrationEvent.OnUploadAvatar -> uploadAvatar(event.context, event.imageUri)
        }
    }

    private fun uploadAvatar(context: Context, imageUri: Uri) {
        viewModelScope.launch {

            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            inputStream?.use { stream ->
                val byteArray = stream.readBytes()
                val requestFile = byteArray.toRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData(
                    "image",
                    "avatar.jpg",
                    requestFile
                )

                uploadImageRepository.uploadImage(imagePart).collectLatest { state ->
                    _uploadAvatarUiState.update {
                        state
                    }
                }
            }
        }

    }

    private fun registerUser(user: RegistrationRequest) {
        userRepository.register(user)
            .onStart { _registrationUiState.value = UiState.Loading() }
            .onEach { registeredUser ->
                _registrationUiState.value = UiState.Success(registeredUser)
            }
            .catch { error ->
                _registrationUiState.value =
                    UiState.Error(error.localizedMessage ?: "Registration failed")
            }
            .launchIn(viewModelScope)
    }

    private fun resetState() {
        _uploadAvatarUiState.value = UiState.Idle()
        _registrationUiState.value = UiState.Idle()
    }
}