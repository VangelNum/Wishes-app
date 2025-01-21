package com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.di

import com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.data.api.WishKeyApi
import com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.data.repository.WishKeyKeyRepositoryImpl
import com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.domain.repository.WishKeyRepository
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

    @Provides
    @Singleton
    fun provideWishKeyRepository(wishKeyApi: WishKeyApi): WishKeyRepository {
        return WishKeyKeyRepositoryImpl(wishKeyApi)
    }
}