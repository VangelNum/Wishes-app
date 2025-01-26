package com.vangelnum.wisher.core.utils

import com.google.gson.Gson
import com.vangelnum.wisher.core.data.ErrorResponse
import retrofit2.HttpException
import javax.inject.Inject

class ErrorParser @Inject constructor(
    private val gson: Gson
) {
    fun parseError(e: Exception): String {
        if (e is HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            return try {
                val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                errorResponse.message ?: e.message()
            } catch (jsonException: Exception) {
                e.message()
            }
        } else {
            return e.localizedMessage ?: "Неизвестная ошибка"
        }
    }
}