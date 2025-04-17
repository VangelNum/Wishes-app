package com.vangelnum.wishes.features.keylogshistory.di

import com.vangelnum.wishes.features.keylogshistory.data.api.KeyLogsHistoryApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KeyLogsHistoryModule {
    @Provides
    @Singleton
    fun provideKeyLogsHistoryApi(retrofit: Retrofit): KeyLogsHistoryApi {
        return retrofit.create(KeyLogsHistoryApi::class.java)
    }
}