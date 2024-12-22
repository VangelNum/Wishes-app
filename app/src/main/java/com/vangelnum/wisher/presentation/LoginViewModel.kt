package com.vangelnum.wisher.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.email, emailError = null)
            }
            is LoginEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.password, passwordError = null)
            }
        }
    }

    fun login() {
        val emailResult = validateEmail(_state.value.email)
        val passwordResult = validatePassword(_state.value.password)

        val hasError = listOf(emailResult, passwordResult).any { !it.successful }

        if (hasError) {
            _state.value = _state.value.copy(
                emailError = emailResult.errorMessage,
                passwordError = passwordResult.errorMessage
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // Assuming the "getMe" endpoint requires email and password for authentication.
            // In a real-world scenario, you might have a separate /login endpoint.
            // Here, we're using the provided info to fetch user details as per the prompt.
            val result = userRepository.getMe() // You might need to send credentials here depending on your API
            _state.value = _state.value.copy(isLoading = false)
            when (result.isSuccess) {
                true -> {
                    _uiEvent.emit(UiEvent.Success)
                }
                false -> {
                    _uiEvent.emit(UiEvent.Failure(result.exceptionOrNull()?.localizedMessage ?: "Login failed"))
                }
            }
        }
    }

    private fun validateEmail(email: String): ValidationResult {
        return if (email.isBlank()) {
            ValidationResult(successful = false, errorMessage = "Email cannot be empty")
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ValidationResult(successful = false, errorMessage = "Invalid email format")
        } else {
            ValidationResult(successful = true)
        }
    }

    private fun validatePassword(password: String): ValidationResult {
        return if (password.isBlank()) {
            ValidationResult(successful = false, errorMessage = "Password cannot be empty")
        } else {
            ValidationResult(successful = true)
        }
    }
}

data class LoginState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false
)

sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
}