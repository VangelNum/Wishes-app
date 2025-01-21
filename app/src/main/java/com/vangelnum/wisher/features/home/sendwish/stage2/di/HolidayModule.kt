package com.vangelnum.wisher.features.home.sendwish.stage2.di

import com.vangelnum.wisher.features.home.sendwish.stage2.data.api.HolidayApi
import com.vangelnum.wisher.features.home.sendwish.stage2.data.repository.HolidayRepositoryImpl
import com.vangelnum.wisher.features.home.sendwish.stage2.domain.repository.HolidayRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HolidayModule {
    @Provides
    @Singleton
    fun provideHolidayApi(retrofit: Retrofit): HolidayApi {
        return retrofit.create(HolidayApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHolidayRepository(api: HolidayApi): HolidayRepository {
        return HolidayRepositoryImpl(api)
    }
}