package com.vangelnum.wishes.features.userwishviewhistory.di

import com.vangelnum.wishes.features.userwishviewhistory.data.repository.ViewHistoryRepositoryImpl
import com.vangelnum.wishes.features.userwishviewhistory.domain.repository.ViewHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class ViewHistoryRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsViewHistoryRepository(
        viewHistoryRepositoryImpl: ViewHistoryRepositoryImpl
    ): ViewHistoryRepository
}
