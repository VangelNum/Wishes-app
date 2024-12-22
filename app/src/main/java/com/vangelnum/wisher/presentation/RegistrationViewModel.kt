package com.vangelnum.wisher.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.data.model.UserRequest
import com.vangelnum.wisher.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegistrationState())
    val state = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.NameChanged -> {
                _state.value = _state.value.copy(name = event.name, nameError = null)
            }

            is RegistrationEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.email, emailError = null)
            }

            is RegistrationEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.password, passwordError = null)
            }

            is RegistrationEvent.AvatarUrlChanged -> {
                _state.value = _state.value.copy(avatarUrl = event.avatarUrl)
            }
        }
    }

    fun registerUser() {
        val nameResult = validateName(_state.value.name)
        val emailResult = validateEmail(_state.value.email)
        val passwordResult = validatePassword(_state.value.password)

        val hasError = listOf(nameResult, emailResult, passwordResult).any { !it.successful }

        if (hasError) {
            _state.value = _state.value.copy(
                nameError = nameResult.errorMessage,
                emailError = emailResult.errorMessage,
                passwordError = passwordResult.errorMessage
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = userRepository.register(
                UserRequest(
                    name = _state.value.name,
                    email = _state.value.email,
                    password = _state.value.password,
                    avatarUrl = _state.value.avatarUrl
                )
            )
            _state.value = _state.value.copy(isLoading = false)
            when (result.isSuccess) {
                true -> {
                    _uiEvent.emit(UiEvent.Success)
                }

                false -> {
                    _uiEvent.emit(
                        UiEvent.Failure(
                            result.exceptionOrNull()?.localizedMessage ?: "Registration failed"
                        )
                    )
                }
            }
        }
    }

    private fun validateName(name: String): ValidationResult {
        return if (name.isBlank()) {
            ValidationResult(successful = false, errorMessage = "Name cannot be empty")
        } else {
            ValidationResult(successful = true)
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
        return if (password.length < 6) {
            ValidationResult(
                successful = false,
                errorMessage = "Password must be at least 6 characters"
            )
        } else {
            ValidationResult(successful = true)
        }
    }
}