package com.vangelnum.wisher.features.home.sendwish.stage3.di

import android.content.Context
import com.vangelnum.wisher.features.home.sendwish.stage3.data.api.GenerationImageApi
import com.vangelnum.wisher.features.home.sendwish.stage3.data.api.GenerationTextApi
import com.vangelnum.wisher.features.home.sendwish.stage3.data.api.SendWishApi
import com.vangelnum.wisher.features.home.sendwish.stage3.data.api.UploadImageApi
import com.vangelnum.wisher.features.home.sendwish.stage3.data.repository.SendWishRepositoryImpl
import com.vangelnum.wisher.features.home.sendwish.stage3.domain.repository.SendWishRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SendWIshModule {

    @Singleton
    @Provides
    fun provideGenerationImageApi(
        retrofit: Retrofit
    ): GenerationImageApi {
        return retrofit.create(GenerationImageApi::class.java)
    }

    @Singleton
    @Provides
    fun provideGenerationTextApi(
        retrofit: Retrofit
    ): GenerationTextApi {
        return retrofit.create(GenerationTextApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSendWishApi(
        retrofit: Retrofit
    ): SendWishApi {
        return retrofit.create(SendWishApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUploadImageApi(
        retrofit: Retrofit
    ): UploadImageApi {
        return retrofit.create(UploadImageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSendWishRepository(
        generationImageApi: GenerationImageApi,
        generationTextApi: GenerationTextApi,
        sendWishApi: SendWishApi,
        uploadImageApi: UploadImageApi,
        @ApplicationContext context: Context
    ): SendWishRepository {
        return SendWishRepositoryImpl(
            generationImageApi,
            generationTextApi,
            sendWishApi,
            uploadImageApi,
            context
        )
    }
}