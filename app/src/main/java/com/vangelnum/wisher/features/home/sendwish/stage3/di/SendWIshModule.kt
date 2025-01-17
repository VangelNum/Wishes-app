package com.vangelnum.wisher.features.home.sendwish.stage3.di

import android.content.Context
import com.vangelnum.wisher.features.home.sendwish.stage3.data.api.GenerateImageApi
import com.vangelnum.wisher.features.home.sendwish.stage3.data.api.SendWishApi
import com.vangelnum.wisher.features.home.sendwish.stage3.data.api.UploadImageApi
import com.vangelnum.wisher.features.home.sendwish.stage3.data.repository.SendWishRepositoryImpl
import com.vangelnum.wisher.features.home.sendwish.stage3.domain.repository.SendWishRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SendWIshModule {

    @Provides
    @Singleton
    @Named("DefaultClient")
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        builder.addInterceptor(loggingInterceptor)
        return builder.build()
    }

    @Provides
    @Singleton
    @Named("ImagePollinationAi")
    fun provideImagePollinationRetrofit(
        @Named("DefaultClient") okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://image.pollinations.ai/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGenerateImageApi(
        @Named("ImagePollinationAi") retrofit: Retrofit
    ): GenerateImageApi {
        return retrofit.create(GenerateImageApi::class.java)
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
    @Named("Imgbb")
    fun provideImgbbRetrofit(
        @Named("DefaultClient") okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.imgbb.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideUploadImageApi(
        @Named("Imgbb") retrofit: Retrofit
    ): UploadImageApi {
        return retrofit.create(UploadImageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSendWishRepository(
        generateImageApi: GenerateImageApi,
        sendWishApi: SendWishApi,
        uploadImageApi: UploadImageApi,
        @ApplicationContext context: Context
    ): SendWishRepository {
        return SendWishRepositoryImpl(generateImageApi, sendWishApi, uploadImageApi, context)
    }
}