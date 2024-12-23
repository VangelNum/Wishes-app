package com.vangelnum.wisher.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.auth.data.model.AuthResponse
import com.vangelnum.wisher.features.auth.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _loginUiState = MutableStateFlow<UiState<AuthResponse>>(UiState.Idle)
    val loginUiState = _loginUiState.asStateFlow()

    fun fetchUserInfo(email: String, password: String) {
        viewModelScope.launch {
            _loginUiState.value = UiState.Loading
            userRepository.getUserInfo(email, password).fold(
                onSuccess = { userInfo ->
                    _loginUiState.value = UiState.Success(userInfo)
                },
                onFailure = { error ->
                    _loginUiState.value = UiState.Error(error.localizedMessage ?: "Failed to fetch user info")
                }
            )
        }
    }

    fun backToEmptyState() {
        _loginUiState.value = UiState.Idle
    }
}