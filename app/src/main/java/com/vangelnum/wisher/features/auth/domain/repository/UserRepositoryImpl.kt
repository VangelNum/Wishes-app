package com.vangelnum.wisher.features.auth.domain.repository

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
    private val errorUtils: ErrorUtils
) : UserRepository {
    override suspend fun register(registrationRequest: RegistrationRequest): Result<AuthResponse> {
        return try {
            val response = api.register(registrationRequest)
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

    override suspend fun getUserInfo(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.getUserInfo(email, password)
            Result.success(response)
        } catch (e: HttpException) {
            val errorMessage = errorUtils.parseErrorMessage(e)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}