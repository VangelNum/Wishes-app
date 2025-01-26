package com.vangelnum.wisher.features.profile.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.auth.core.model.AuthResponse
import com.vangelnum.wisher.features.profile.domain.repository.UpdateProfileRepository
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

    fun updateProfile(name: String?, email: String?, password: String?, avatar: Uri?, context: Context) {
        viewModelScope.launch {
            updateProfileRepository.updateUserProfile(name, email, password, avatar, context).collect { state->
                _updateProfileState.update { state }
            }
        }
    }
}