package com.vangelnum.wisher.features.auth.presentation

import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.auth.data.model.AuthResponse
import com.vangelnum.wisher.features.auth.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val authorizationHeaderKey = stringPreferencesKey("authorization_header")

    private val _loginUiState = MutableStateFlow<UiState<AuthResponse>>(UiState.Loading)
    val loginUiState = _loginUiState.asStateFlow()

    init {
        onEvent(LoginEvent.onEnterApp)
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            LoginEvent.onBackToEmptyState -> {
                backToEmptyState()
            }

            is LoginEvent.onLoginUser -> {
                getUserInfo(event.email, event.password)
            }

            LoginEvent.onExit -> {
                viewModelScope.launch {
                    clearAuthorizationHeader()
                    _loginUiState.value = UiState.Idle
                }
            }

            LoginEvent.onEnterApp -> {
                viewModelScope.launch {
                    attemptAutoLogin()
                }
            }
        }
    }

    private suspend fun attemptAutoLogin() {
        val storedHeader = dataStore.data.first()[authorizationHeaderKey]
        if (storedHeader != null) {
            userRepository.getUserInfo(storedHeader).onSuccess {
                _loginUiState.value = UiState.Success(it)
            }.onFailure {
                _loginUiState.value = UiState.Idle
            }
        } else {
            _loginUiState.value = UiState.Idle
        }
    }

    private fun getUserInfo(email: String, password: String) {
        _loginUiState.value = UiState.Loading
        viewModelScope.launch {
            val credentials = "${email}:${password}"
            val base64Credentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            val authorizationHeader = "Basic $base64Credentials"
            userRepository.getUserInfo(authorizationHeader).onSuccess {
                _loginUiState.value = UiState.Success(it)
                saveAuthorizationHeader(authorizationHeader)
            }.onFailure {
                _loginUiState.value = UiState.Error(it.localizedMessage ?: "An unexpected error occurred")
            }
        }
    }

    private suspend fun saveAuthorizationHeader(header: String) {
        dataStore.edit { preferences ->
            preferences[authorizationHeaderKey] = header
        }
    }

    private suspend fun clearAuthorizationHeader() {
        dataStore.edit { preferences ->
            preferences.remove(authorizationHeaderKey)
        }
    }

    private fun backToEmptyState() {
        _loginUiState.value = UiState.Idle
    }
}