package com.vangelnum.wisher.features.home.sendwish.createwish.di

import com.vangelnum.wisher.features.home.sendwish.createwish.data.api.GenerationImageApi
import com.vangelnum.wisher.features.home.sendwish.createwish.data.api.GenerationTextApi
import com.vangelnum.wisher.features.home.sendwish.createwish.data.api.SendWishApi
import com.vangelnum.wisher.features.home.sendwish.createwish.data.api.UploadImageApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}