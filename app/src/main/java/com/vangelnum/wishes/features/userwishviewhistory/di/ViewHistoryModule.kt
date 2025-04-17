package com.vangelnum.wishes.features.userwishviewhistory.di

import com.vangelnum.wishes.features.userwishviewhistory.data.api.ViewHistoryApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewHistoryModule {
    @Provides
    @Singleton
    fun provideViewHistoryApi(retrofit: Retrofit): ViewHistoryApi {
        return retrofit.create(ViewHistoryApi::class.java)
    }
}