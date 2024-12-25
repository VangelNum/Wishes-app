package com.vangelnum.wisher.features.auth.domain.repository

import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vangelnum.wisher.core.utils.ErrorUtils
import com.vangelnum.wisher.features.auth.api.AuthApi
import com.vangelnum.wisher.features.auth.data.model.AuthResponse
import com.vangelnum.wisher.features.auth.data.model.RegistrationRequest
import com.vangelnum.wisher.features.auth.data.repository.UserRepository
import retrofit2.HttpException
import java.net.SocketTimeoutException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val errorUtils: ErrorUtils,
    private val dataStore: DataStore<Preferences>
) : UserRepository {

    private val authorizationHeaderKey = stringPreferencesKey("authorization_header")

    override suspend fun register(registrationRequest: RegistrationRequest): Result<AuthResponse> {
        return try {
            val credentials = "${registrationRequest.email}:${registrationRequest.password}"
            val base64Credentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            val authorizationHeader = "Basic $base64Credentials"
            val response = api.register(registrationRequest)
            saveAuthorizationHeader(authorizationHeader)
            Result.success(response)
        } catch (e: HttpException) {
            val errorMessage = errorUtils.parseErrorMessage(e)
            Result.failure(Exception(errorMessage))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Время ожидания истекло, попробуйте зайти позже"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserInfo(authorizationHeader: String): Result<AuthResponse> {
        return try {
            val response = api.getUserInfo(authorizationHeader)
            saveAuthorizationHeader(authorizationHeader)
            Result.success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                Result.failure(Exception("Неверные учетные данные"))
            } else {
                val errorMessage = errorUtils.parseErrorMessage(e)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Время ожидания истекло, попробуйте зайти позже"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveAuthorizationHeader(header: String) {
        dataStore.edit { preferences ->
            preferences[authorizationHeaderKey] = header
        }
    }
}