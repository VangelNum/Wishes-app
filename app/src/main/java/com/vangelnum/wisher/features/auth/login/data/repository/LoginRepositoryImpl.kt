package com.vangelnum.wisher.features.auth.login.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vangelnum.wisher.features.auth.core.model.AuthResponse
import com.vangelnum.wisher.features.auth.login.data.api.LoginApi
import com.vangelnum.wisher.features.auth.login.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginApi: LoginApi,
    private val dataStore: DataStore<Preferences>
) : LoginRepository {

    private val authorizationHeaderKey = stringPreferencesKey("authorization_header")

    override fun loginUser(authorizationHeader: String): Flow<AuthResponse> = flow {
        val response = loginApi.getUserInfo(authorizationHeader)
        saveAuthorizationHeader(authorizationHeader)
        emit(response)
    }

    private suspend fun saveAuthorizationHeader(header: String) {
        dataStore.edit { preferences ->
            preferences[authorizationHeaderKey] = header
        }
    }

    override fun refreshUserData(authorizationHeader: String): Flow<AuthResponse> = flow {
        try {
            val response = loginApi.getUserInfo(authorizationHeader)
            emit(response)
        } catch (e: HttpException) {
            throw e
        } catch (e: Exception) {
            throw e
        }
    }
}