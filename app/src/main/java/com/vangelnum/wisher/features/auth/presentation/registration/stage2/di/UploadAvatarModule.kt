package com.vangelnum.wisher.features.auth.presentation.registration.stage2.di

import com.vangelnum.wisher.features.auth.presentation.registration.stage2.data.repository.UploadImageRepositoryImpl
import com.vangelnum.wisher.features.auth.presentation.registration.stage2.domain.repository.UploadImageRepository
import com.vangelnum.wisher.features.home.sendwish.stage3.data.api.UploadImageApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UploadAvatarModule {
    @Provides
    @Singleton
    fun provideUploadImageRepository(api: UploadImageApi): UploadImageRepository {
        return UploadImageRepositoryImpl(api)
    }
}

