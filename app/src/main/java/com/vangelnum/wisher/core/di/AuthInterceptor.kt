package com.vangelnum.wisher.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val authorizationHeaderKey = stringPreferencesKey("authorization_header")
        val authorizationHeader = runBlocking {
            dataStore.data.first()[authorizationHeaderKey]
        }

        val requestBuilder = originalRequest.newBuilder()
            .apply {
                if (!originalRequest.url.toString().endsWith("/api/v1/user/register") && !originalRequest.url.toString().endsWith("/api/v1/user/me")) {
                    authorizationHeader?.let { header ->
                        header("Authorization", header)
                    }
                }
            }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}