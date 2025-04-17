package com.vangelnum.wishes.features.keylogshistory.di

import com.vangelnum.wishes.features.keylogshistory.data.repository.KeyLogsHistoryRepositoryImpl
import com.vangelnum.wishes.features.keylogshistory.domain.repository.KeyLogsHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class KeyLogsHistoryRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsKeyLogsHistoryRepository(
        keyLogsHistoryRepositoryImpl: KeyLogsHistoryRepositoryImpl
    ): KeyLogsHistoryRepository
}
