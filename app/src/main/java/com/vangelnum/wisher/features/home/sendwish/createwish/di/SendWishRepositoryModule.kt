package com.vangelnum.wisher.features.home.sendwish.createwish.di

import com.vangelnum.wisher.features.home.sendwish.createwish.data.repository.SendWishRepositoryImpl
import com.vangelnum.wisher.features.home.sendwish.createwish.domain.repository.SendWishRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SendWishRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSendWishRepository(sendWishRepositoryImpl: SendWishRepositoryImpl): SendWishRepository
}