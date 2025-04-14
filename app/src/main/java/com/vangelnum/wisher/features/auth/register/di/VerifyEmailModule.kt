package com.vangelnum.wisher.features.auth.register.di

import com.vangelnum.wisher.features.auth.register.data.api.VerifyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VerifyEmailModule {

    @Provides
    @Singleton
    fun provideVerifyApi(
        retrofit: Retrofit
    ): VerifyApi {
        return retrofit.create(VerifyApi::class.java)
    }
}