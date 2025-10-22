package com.vangelnum.wishes.features.editprofile.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.auth.core.model.AuthResponse
import com.vangelnum.wishes.features.editprofile.domain.repository.EditProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val editProfileRepository: EditProfileRepository
): ViewModel() {
    private val _editProfileState = MutableStateFlow<UiState<AuthResponse>>(UiState.Idle())
    val editProfileState = _editProfileState.asStateFlow()

    private val _uploadAvatarState = MutableStateFlow<UiState<String>>(UiState.Idle())
    val uploadAvatarState = _uploadAvatarState.asStateFlow()

    fun uploadAvatar(imageUri: Uri, context: Context) {
        viewModelScope.launch {
            editProfileRepository.uploadProfileImage(imageUri, context).collect { state->
                _uploadAvatarState.update { state }
            }
        }
    }

    fun editProfile(
        name: String?,
        email: String?,
        avatarUrl: String?,
        newPassword: String?,
        currentPassword: String?
    ) {
        viewModelScope.launch {
            val finalNewPassword = newPassword?.ifBlank { null }
            val finalCurrentPassword = currentPassword?.ifBlank { null }

            editProfileRepository.updateUserProfile(
                name = name,
                email = email,
                avatarUrl = avatarUrl,
                currentPassword = finalCurrentPassword,
                newPassword = finalNewPassword
            ).collect { state->
                _editProfileState.update { state }
            }
        }
    }

    fun backToEmptyState() {
        _editProfileState.update {
            UiState.Idle()
        }
        _uploadAvatarState.update {
            UiState.Idle()
        }
    }
}