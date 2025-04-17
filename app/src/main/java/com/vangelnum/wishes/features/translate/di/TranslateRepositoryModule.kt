package com.vangelnum.wishes.features.translate.di

import com.vangelnum.wishes.features.translate.data.repsitory.TranslateRepositoryImpl
import com.vangelnum.wishes.features.translate.domain.repository.TranslateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TranslateRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsTranslateRepository(translateRepositoryImpl: TranslateRepositoryImpl): TranslateRepository
}