package com.vangelnum.wisher.features.auth.register.data.repository

import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.utils.ErrorParser
import com.vangelnum.wisher.features.auth.register.data.api.RegisterApi
import com.vangelnum.wisher.features.auth.register.data.model.RegistrationRequest
import com.vangelnum.wisher.features.auth.register.domain.repository.RegisterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val registerApi: RegisterApi,
    private val dataStore: DataStore<Preferences>,
    private val errorParser: ErrorParser
): RegisterRepository {

    private val authorizationHeaderKey = stringPreferencesKey("authorization_header")

    override fun register(registrationRequest: RegistrationRequest): Flow<UiState<String>> = flow {
        emit(UiState.Loading())
        try {
            val credentials = "${registrationRequest.email}:${registrationRequest.password}"
            val base64Credentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            val authorizationHeader = "Basic $base64Credentials"
            val response = registerApi.register(registrationRequest)
            saveAuthorizationHeader(authorizationHeader)
            emit(UiState.Success(response))
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.parseError(e)))
        }
    }

    private suspend fun saveAuthorizationHeader(header: String) {
        dataStore.edit { preferences ->
            preferences[authorizationHeaderKey] = header
        }
    }
}