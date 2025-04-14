package com.vangelnum.wisher.features.home.getwish.di

import com.vangelnum.wisher.features.home.getwish.data.api.GetWishApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GetWishModule {
    @Provides
    @Singleton
    fun provideGetWishApi(retrofit: Retrofit): GetWishApi {
        return retrofit.create(GetWishApi::class.java)
    }
}