package com.vangelnum.wisher.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.auth.data.model.AuthResponse
import com.vangelnum.wisher.features.auth.data.model.RegistrationRequest
import com.vangelnum.wisher.features.auth.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _registrationState = MutableStateFlow<UiState<AuthResponse>>(UiState.Idle)
    val registrationState = _registrationState.asStateFlow()

    fun onEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.OnRegisterUser -> {
                registerUser(event.user)
            }

            RegistrationEvent.OnBackToEmptyState -> {
                _registrationState.value = UiState.Idle
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
