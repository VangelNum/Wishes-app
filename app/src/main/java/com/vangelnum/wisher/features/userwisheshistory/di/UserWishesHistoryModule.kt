package com.vangelnum.wisher.features.userwisheshistory.di

import com.vangelnum.wisher.core.utils.ErrorParser
import com.vangelnum.wisher.features.userwisheshistory.data.api.UserWishesHistoryApi
import com.vangelnum.wisher.features.userwisheshistory.data.repository.UserWishesHistoryRepositoryImpl
import com.vangelnum.wisher.features.userwisheshistory.domain.repository.UserWishesHistoryRepository
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

    @Provides
    @Singleton
    fun provideUserWishesRepository(
        api: UserWishesHistoryApi,
        errorParser: ErrorParser
    ): UserWishesHistoryRepository {
        return UserWishesHistoryRepositoryImpl(api, errorParser)
    }
}