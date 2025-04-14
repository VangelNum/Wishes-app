package com.vangelnum.wisher.features.userwishsendinghistory.di

import com.vangelnum.wisher.features.userwishsendinghistory.data.api.UserWishesHistoryApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserWishesHistoryModule {

    @Provides
    @Singleton
    fun provideUserWishesHistoryApi(retrofit: Retrofit): UserWishesHistoryApi {
        return retrofit.create(UserWishesHistoryApi::class.java)
    }
}