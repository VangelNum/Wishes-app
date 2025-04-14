package com.vangelnum.wisher.features.home.sendwish.selectdate.wishkey.di

import com.vangelnum.wisher.features.home.sendwish.selectdate.wishkey.data.api.WishKeyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WishKeyModule {
    @Provides
    @Singleton
    fun provideWishKeyApi(retrofit: Retrofit): WishKeyApi {
        return retrofit.create(WishKeyApi::class.java)
    }
}