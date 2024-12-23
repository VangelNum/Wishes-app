package com.vangelnum.wisher.core.utils

import com.google.gson.Gson
import com.vangelnum.wisher.core.data.ErrorResponse
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorUtils @Inject constructor(
    private val gson: Gson
) {
    fun parseErrorMessage(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
            errorResponse.message ?: "Unknown error"
        } catch (exception: Exception) {
            "An unexpected error occurred ${e.message}"
        }
    }
}