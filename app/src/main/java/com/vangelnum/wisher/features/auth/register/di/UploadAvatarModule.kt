package com.vangelnum.wisher.features.auth.register.di

import com.vangelnum.wisher.core.utils.ErrorParser
import com.vangelnum.wisher.features.auth.register.data.api.RegisterApi
import com.vangelnum.wisher.features.auth.register.data.repository.UploadImageRepositoryImpl
import com.vangelnum.wisher.features.auth.register.domain.repository.UploadImageRepository
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
    fun provideUploadImageRepository(
        api: UploadImageApi,
        registerApi: RegisterApi,
        errorParser: ErrorParser
    ): UploadImageRepository {
        return UploadImageRepositoryImpl(
            apiService = api,
            registerApi = registerApi,
            errorParser = errorParser
        )
    }
}