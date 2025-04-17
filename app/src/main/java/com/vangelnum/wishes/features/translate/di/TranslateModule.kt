package com.vangelnum.wishes.features.translate.di

import com.vangelnum.wishes.features.translate.data.model.api.TranslateApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TranslateModule {
    @Provides
    @Singleton
    fun provideTranslateApi(retrofit: Retrofit): TranslateApi = retrofit.create(TranslateApi::class.java)
}