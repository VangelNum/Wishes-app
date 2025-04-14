package com.vangelnum.wisher.features.home.sendwish.selectholiday.di

import com.vangelnum.wisher.features.home.sendwish.selectholiday.data.repository.HolidayRepositoryImpl
import com.vangelnum.wisher.features.home.sendwish.selectholiday.domain.repository.HolidayRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class HolidayRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsHolidayRepository(holidayRepositoryImpl: HolidayRepositoryImpl): HolidayRepository
}
