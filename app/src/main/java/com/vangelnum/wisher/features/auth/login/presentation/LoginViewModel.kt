package com.vangelnum.wisher.features.auth.login.presentation

import android.util.Base64
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.data.UiState.Idle
import com.vangelnum.wisher.features.auth.core.model.AuthResponse
import com.vangelnum.wisher.features.auth.login.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val authorizationHeaderKey = stringPreferencesKey("authorization_header")

    private val _loginUiState = MutableStateFlow<UiState<AuthResponse>>(UiState.Loading())
    val loginUiState = _loginUiState.asStateFlow()

    init {
        onEvent(LoginEvent.OnEnterApp)
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            LoginEvent.OnBackToEmptyState -> {
                backToEmptyState()
            }

            is LoginEvent.OnLoginUser -> {
                getUserInfo(event.email, event.password)
            }

            LoginEvent.OnExit -> {
                _loginUiState.value = Idle()
                viewModelScope.launch {
                    clearAuthorizationHeader()
                }
            }

            LoginEvent.OnEnterApp -> {
                viewModelScope.launch {
                    attemptAutoLogin()
                }
            }

            LoginEvent.OnRefreshUser -> {
                refreshUser()
            }
        }
    }

    private fun refreshUser() {
        viewModelScope.launch {
            dataStore.data.first()[authorizationHeaderKey]?.let { storedHeader ->
                loginRepository.refreshUserData(storedHeader)
                    .onStart {}
                    .catch { error ->
                        Log.d("RefreshUser","Error refreshing user data: ${error.localizedMessage}")
                    }
                    .collect { authResponse ->
                        _loginUiState.value = UiState.Success(authResponse)
                    }
            }
        }
    }

    private suspend fun attemptAutoLogin() {
        dataStore.data.first()[authorizationHeaderKey]?.let { storedHeader ->
            loginRepository.loginUser(storedHeader)
                .onStart { _loginUiState.value = UiState.Loading() }
                .catch {
                    _loginUiState.value =
                        UiState.Error(it.localizedMessage ?: "An unexpected error occurred")
                }
                .collect {
                    _loginUiState.value = UiState.Success(it)
                }
        } ?: run {
            _loginUiState.value = Idle()
        }
    }

    private fun getUserInfo(email: String, password: String) {
        val credentials = "${email.trim()}:${password.trim()}"
        val base64Credentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        val authorizationHeader = "Basic $base64Credentials"

        viewModelScope.launch {
            loginRepository.loginUser(authorizationHeader)
                .onStart { _loginUiState.value = UiState.Loading() }
                .catch { error ->
                    if (error is HttpException && error.code() == 401) {
                        _loginUiState.value = UiState.Error("Invalid credentials or user does not exist")
                    } else {
                        _loginUiState.value =
                            UiState.Error(error.localizedMessage ?: "An unexpected error occurred")
                    }
                }
                .collect { authResponse ->
                    _loginUiState.value = UiState.Success(authResponse)
                    saveAuthorizationHeader(authorizationHeader)
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
        _loginUiState.value = Idle()
    }
}