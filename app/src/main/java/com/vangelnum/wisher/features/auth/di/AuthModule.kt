package com.vangelnum.wisher.features.auth.di

import com.vangelnum.wisher.core.utils.ErrorUtils
import com.vangelnum.wisher.features.auth.api.AuthApi
import com.vangelnum.wisher.features.auth.data.repository.UserRepository
import com.vangelnum.wisher.features.auth.domain.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(api: AuthApi, errorUtils: ErrorUtils): UserRepository {
        return UserRepositoryImpl(api, errorUtils)
    }
}