package com.vangelnum.wisher.features.home.sendwish.selectholiday.di

import com.vangelnum.wisher.features.home.sendwish.selectholiday.data.api.HolidayApi
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
}