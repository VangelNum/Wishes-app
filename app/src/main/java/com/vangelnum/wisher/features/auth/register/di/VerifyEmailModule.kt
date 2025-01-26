package com.vangelnum.wisher.features.auth.register.di

import com.vangelnum.wisher.core.utils.ErrorParser
import com.vangelnum.wisher.features.auth.register.data.api.VerifyApi
import com.vangelnum.wisher.features.auth.register.data.repository.VerifyEmailRepositoryImpl
import com.vangelnum.wisher.features.auth.register.domain.repository.VerifyEmailRepository
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

    @Provides
    @Singleton
    fun provideVerifyEmailRepository(
        api: VerifyApi,
        errorParser: ErrorParser
    ): VerifyEmailRepository {
        return VerifyEmailRepositoryImpl(api, errorParser)
    }
}