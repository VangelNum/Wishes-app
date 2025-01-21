package com.vangelnum.wisher.features.auth.data.repository

import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vangelnum.wisher.features.auth.api.AuthApi
import com.vangelnum.wisher.features.auth.data.model.AuthResponse
import com.vangelnum.wisher.features.auth.data.model.RegistrationRequest
import com.vangelnum.wisher.features.auth.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val dataStore: DataStore<Preferences>
) : UserRepository {

    private val authorizationHeaderKey = stringPreferencesKey("authorization_header")

    override fun register(registrationRequest: RegistrationRequest): Flow<AuthResponse> = flow {
        val credentials = "${registrationRequest.email}:${registrationRequest.password}"
        val base64Credentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        val authorizationHeader = "Basic $base64Credentials"
        val response = api.register(registrationRequest)
        saveAuthorizationHeader(authorizationHeader)
        emit(response)
    }

    override fun getUserInfo(authorizationHeader: String): Flow<AuthResponse> = flow {
        val response = api.getUserInfo(authorizationHeader)
        saveAuthorizationHeader(authorizationHeader)
        emit(response)
    }

    private suspend fun saveAuthorizationHeader(header: String) {
        dataStore.edit { preferences ->
            preferences[authorizationHeaderKey] = header
        }
    }
}