package com.vangelnum.wishes.features.editprofile.di

import com.vangelnum.wishes.features.editprofile.data.repository.EditProfileRepositoryImpl
import com.vangelnum.wishes.features.editprofile.domain.repository.EditProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EditProfileRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsEditProfileRepository(
        editProfileRepositoryImpl: EditProfileRepositoryImpl
    ): EditProfileRepository
}
