package com.vangelnum.wisher.features.profile.di

import com.vangelnum.wisher.features.profile.data.repository.UpdateProfileRepositoryImpl
import com.vangelnum.wisher.features.profile.domain.repository.UpdateProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsProfileRepository(
        updateProfileRepositoryImpl: UpdateProfileRepositoryImpl
    ): UpdateProfileRepository
}
