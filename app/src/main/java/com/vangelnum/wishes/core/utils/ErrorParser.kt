package com.vangelnum.wishes.core.utils

import com.google.gson.Gson
import com.vangelnum.wishes.core.data.ErrorResponse
import retrofit2.HttpException
import javax.inject.Inject

class ErrorParser @Inject constructor(
    private val gson: Gson
) {
    fun parseError(e: Exception): String {
        if (e is HttpException) {
            if (e.code() == 401) {
                return "You are not logged in"
            }
            if (e.code() == 502 || e.code() == 503) {
                return "Technical work. Please check back later."
            }
            val errorBody = e.response()?.errorBody()?.string()
            return try {
                val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                errorResponse.message ?: e.message()
            } catch (jsonException: Exception) {
                jsonException.printStackTrace()
                e.message()
            }
        } else {
            return e.localizedMessage ?: "Unknown error"
        }
    }
}