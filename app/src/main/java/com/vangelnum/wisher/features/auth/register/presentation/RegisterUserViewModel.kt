package com.vangelnum.wisher.features.auth.register.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.auth.core.model.AuthResponse
import com.vangelnum.wisher.features.auth.register.data.model.RegistrationRequest
import com.vangelnum.wisher.features.auth.register.domain.repository.RegisterRepository
import com.vangelnum.wisher.features.auth.register.domain.repository.UploadImageRepository
import com.vangelnum.wisher.features.auth.register.domain.repository.VerifyEmailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel @Inject constructor(
    private val uploadImageRepository: UploadImageRepository,
    private val registerRepository: RegisterRepository,
    private val verificationRepository: VerifyEmailRepository
) : ViewModel() {
    private val _uploadAvatarUiState = MutableStateFlow<UiState<String>>(UiState.Idle())
    val uploadAvatarUiState: StateFlow<UiState<String>> = _uploadAvatarUiState.asStateFlow()

    private val _pendingRegistrationUiState = MutableStateFlow<UiState<String>>(UiState.Idle())
    val pendingRegistrationUiState = _pendingRegistrationUiState.asStateFlow()

    private val _updateAvatarUiState = MutableStateFlow<UiState<AuthResponse>>(UiState.Idle())
    val updateAvatarUiState = _updateAvatarUiState.asStateFlow()

    private val _registrationUiState = MutableStateFlow<UiState<AuthResponse>>(UiState.Idle())
    val registrationUiState = _registrationUiState.asStateFlow()

    fun onEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.OnRegisterUser -> registerUser(event.user)
            RegistrationEvent.OnBackToEmptyState -> resetState()
            is RegistrationEvent.OnUploadAvatar -> uploadAvatar(event.context, event.imageUri)
            is RegistrationEvent.OnUpdateAvatar -> updateUserAvatar(event.imageUri)
            is RegistrationEvent.OnVerifyEmail -> verifyEmail(event.email, event.verificationCode)
        }
    }


    private fun registerUser(user: RegistrationRequest) {
        viewModelScope.launch {
            registerRepository.register(user).collect { state ->
                _pendingRegistrationUiState.update { state }
            }
        }
    }

    private fun verifyEmail(email: String, verificationCode: String) {
        viewModelScope.launch {
            verificationRepository.verifyEmail(email, verificationCode).collect { state ->
                _registrationUiState.update { state }
            }
        }
    }

    private fun uploadAvatar(context: Context, imageUri: Uri) {
        val seed = UUID.randomUUID()
        viewModelScope.launch {
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            inputStream?.use { stream ->
                val byteArray = stream.readBytes()
                val requestFile = byteArray.toRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData(
                    "image",
                    "avatar_$seed.jpg",
                    requestFile
                )

                uploadImageRepository.uploadImage(imagePart).collect { state ->
                    _uploadAvatarUiState.update { state }
                }
            }
        }
    }

    private fun updateUserAvatar(imageUri: String) {
        _updateAvatarUiState.let { _ ->
            uploadImageRepository.updateAvatar(imageUri)
                .onStart { _updateAvatarUiState.value = UiState.Loading() }
                .onEach { response ->
                    _updateAvatarUiState.value = UiState.Success(response)
                }
                .catch { error ->
                    _updateAvatarUiState.value =
                        UiState.Error(error.localizedMessage ?: "Не удалось обновить аватар")
                }
                .launchIn(viewModelScope)
        }
    }

    private fun resetState() {
        _uploadAvatarUiState.value = UiState.Idle()
        _pendingRegistrationUiState.value = UiState.Idle()
        _updateAvatarUiState.value = UiState.Idle()
    }
}