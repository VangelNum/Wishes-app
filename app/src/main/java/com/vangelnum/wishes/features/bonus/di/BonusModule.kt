package com.vangelnum.wishes.features.bonus.di

import com.vangelnum.wishes.features.bonus.data.api.BonusApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BonusModule {
    @Provides
    @Singleton
    fun provideBonusApi(retrofit: Retrofit): BonusApi {
        return retrofit.create(BonusApi::class.java)
    }
}