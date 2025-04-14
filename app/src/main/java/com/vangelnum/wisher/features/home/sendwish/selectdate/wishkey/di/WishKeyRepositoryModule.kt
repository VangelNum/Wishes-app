package com.vangelnum.wisher.features.home.sendwish.selectdate.wishkey.di

import com.vangelnum.wisher.features.home.sendwish.selectdate.wishkey.data.repository.WishKeyRepositoryImpl
import com.vangelnum.wisher.features.home.sendwish.selectdate.wishkey.domain.repository.WishKeyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WishKeyRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsWishKeyRepository(wishKeyRepositoryImpl: WishKeyRepositoryImpl): WishKeyRepository
}