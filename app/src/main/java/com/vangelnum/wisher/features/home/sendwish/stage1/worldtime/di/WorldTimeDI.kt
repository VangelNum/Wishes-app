package com.vangelnum.wisher.features.home.sendwish.stage1.worldtime.di

import com.vangelnum.wisher.features.home.sendwish.stage1.worldtime.data.api.WorldTimeApi
import com.vangelnum.wisher.features.home.sendwish.stage1.worldtime.data.repository.WorldTimeRepositoryImpl
import com.vangelnum.wisher.features.home.sendwish.stage1.worldtime.domain.repository.WorldTimeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorldTimeDI {

    @Provides
    @Singleton
    @Named("WorldTimeApi")
    fun provideWorldTimeRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://tools.aimylogic.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWorldTimeApi(
        @Named("WorldTimeApi") retrofit: Retrofit
    ): WorldTimeApi {
        return retrofit.create(WorldTimeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWorldTimeRepository(worldTimeApi: WorldTimeApi): WorldTimeRepository {
        return WorldTimeRepositoryImpl(worldTimeApi)
    }
}