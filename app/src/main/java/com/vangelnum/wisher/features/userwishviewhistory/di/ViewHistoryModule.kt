package com.vangelnum.wisher.features.userwishviewhistory.di

import com.vangelnum.wisher.core.utils.ErrorParser
import com.vangelnum.wisher.features.userwishviewhistory.data.api.ViewHistoryApi
import com.vangelnum.wisher.features.userwishviewhistory.data.repository.ViewHistoryRepositoryImpl
import com.vangelnum.wisher.features.userwishviewhistory.domain.repository.ViewHistoryRepository
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

    @Provides
    @Singleton
    fun provideViewHistoryRepository(
        api: ViewHistoryApi,
        errorParser: ErrorParser
    ): ViewHistoryRepository {
        return ViewHistoryRepositoryImpl(api, errorParser)
    }
}