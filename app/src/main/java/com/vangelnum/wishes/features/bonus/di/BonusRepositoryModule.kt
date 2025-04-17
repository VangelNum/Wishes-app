package com.vangelnum.wishes.features.bonus.di

import com.vangelnum.wishes.features.bonus.data.repository.BonusRepositoryImpl
import com.vangelnum.wishes.features.bonus.domain.repository.BonusRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BonusRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsBonusRepository(repository: BonusRepositoryImpl): BonusRepository
}