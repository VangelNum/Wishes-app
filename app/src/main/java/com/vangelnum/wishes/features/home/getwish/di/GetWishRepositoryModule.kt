package com.vangelnum.wishes.features.home.getwish.di

import com.vangelnum.wishes.features.home.getwish.data.repository.GetWishRepositoryImpl
import com.vangelnum.wishes.features.home.getwish.domain.repository.GetWishRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GetWishRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindGetWishRepository(
        getWishRepositoryImpl: GetWishRepositoryImpl
    ): GetWishRepository
}