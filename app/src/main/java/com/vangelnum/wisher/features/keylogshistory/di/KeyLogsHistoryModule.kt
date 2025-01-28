package com.vangelnum.wisher.features.keylogshistory.di

import com.vangelnum.wisher.core.utils.ErrorParser
import com.vangelnum.wisher.features.keylogshistory.data.api.KeyLogsHistoryApi
import com.vangelnum.wisher.features.keylogshistory.data.repository.KeyLogsHistoryRepositoryImpl
import com.vangelnum.wisher.features.keylogshistory.domain.repository.KeyLogsHistoryRepository
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

    @Singleton
    @Provides
    fun provideKeyLogsHistoryRepository(
        api: KeyLogsHistoryApi,
        errorParser: ErrorParser
    ): KeyLogsHistoryRepository {
        return KeyLogsHistoryRepositoryImpl(api, errorParser)
    }
}