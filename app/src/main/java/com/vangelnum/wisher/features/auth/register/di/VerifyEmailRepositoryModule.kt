package com.vangelnum.wisher.features.auth.register.di

import com.vangelnum.wisher.features.auth.register.data.repository.VerifyEmailRepositoryImpl
import com.vangelnum.wisher.features.auth.register.domain.repository.VerifyEmailRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VerifyEmailRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsVerifyEmailRepository(
        verifyEmailRepositoryImpl: VerifyEmailRepositoryImpl
    ): VerifyEmailRepository
}