package com.vangelnum.wishes.features.userwishsendinghistory.di

import com.vangelnum.wishes.features.userwishsendinghistory.data.repository.UserWishesHistoryRepositoryImpl
import com.vangelnum.wishes.features.userwishsendinghistory.domain.repository.UserWishesHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserWishesHistoryRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsUserWishesHistoryRepository(
        userWishesHistoryRepositoryImpl: UserWishesHistoryRepositoryImpl
    ): UserWishesHistoryRepository
}
