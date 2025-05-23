package com.vangelnum.wishes.features.home.sendwish.selectdate.worldtime.di

import com.vangelnum.wishes.features.home.sendwish.selectdate.worldtime.data.api.WorldTimeApi
import com.vangelnum.wishes.features.home.sendwish.selectdate.worldtime.data.repository.WorldTimeRepositoryImpl
import com.vangelnum.wishes.features.home.sendwish.selectdate.worldtime.domain.repository.WorldTimeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorldTimeDI {

    @Provides
    @Singleton
    fun provideWorldTimeApi(
        retrofit: Retrofit
    ): WorldTimeApi {
        return retrofit.create(WorldTimeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWorldTimeRepository(worldTimeApi: WorldTimeApi): WorldTimeRepository {
        return WorldTimeRepositoryImpl(worldTimeApi)
    }
}