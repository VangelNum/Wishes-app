package com.vangelnum.wishes.features.profile.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.auth.core.model.AuthResponse
import com.vangelnum.wishes.features.profile.domain.repository.UpdateProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val updateProfileRepository: UpdateProfileRepository
): ViewModel() {
    private val _updateProfileState = MutableStateFlow<UiState<AuthResponse>>(UiState.Idle())
    val updateProfileState = _updateProfileState.asStateFlow()

    private val _uploadAvatarState = MutableStateFlow<UiState<String>>(UiState.Idle())
    val uploadAvatarState = _uploadAvatarState.asStateFlow()

    fun uploadAvatar(imageUri: Uri, context: Context) {
        viewModelScope.launch {
            updateProfileRepository.uploadProfileImage(imageUri, context).collect { state->
                _uploadAvatarState.update { state }
            }
        }
    }

    fun updateProfile(name: String?, email: String?, password: String?, currentPassword: String?, avatar: String?) {
        viewModelScope.launch {
            updateProfileRepository.updateUserProfile(name, email, password, currentPassword, avatar).collect { state->
                _updateProfileState.update { state }
            }
        }
    }

    fun backToEmptyState() {
        _updateProfileState.update {
            UiState.Idle()
        }
        _uploadAvatarState.update {
            UiState.Idle()
        }
    }
}