package com.vangelnum.wisher.features.auth.register.di

import com.vangelnum.wisher.features.auth.register.data.repository.UploadImageRepositoryImpl
import com.vangelnum.wisher.features.auth.register.domain.repository.UploadImageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UploadImageRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUploadImageRepository(uploadImageRepositoryImpl: UploadImageRepositoryImpl): UploadImageRepository
}