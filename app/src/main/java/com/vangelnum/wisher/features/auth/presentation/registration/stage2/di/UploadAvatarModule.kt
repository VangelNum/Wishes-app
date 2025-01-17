package com.vangelnum.wisher.features.auth.presentation.registration.stage2.di

import com.vangelnum.wisher.features.auth.presentation.registration.stage2.data.api.UploadAvatarService
import com.vangelnum.wisher.features.auth.presentation.registration.stage2.data.repository.UploadAvatarRepositoryImpl
import com.vangelnum.wisher.features.auth.presentation.registration.stage2.domain.repository.UploadAvatarRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UploadAvatarModule {

    @Provides
    @Singleton
    fun provideUploadAvatarApi(): UploadAvatarService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.imgbb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(UploadAvatarService::class.java)
    }

    @Provides
    @Singleton
    fun provideUploadAvatarRepository(apiService: UploadAvatarService): UploadAvatarRepository {
        return UploadAvatarRepositoryImpl(apiService)
    }
}

