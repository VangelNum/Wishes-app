package com.vangelnum.wisher.features.profile.di

import com.vangelnum.wisher.core.utils.ErrorParser
import com.vangelnum.wisher.features.home.sendwish.stage3.data.api.UploadImageApi
import com.vangelnum.wisher.features.profile.data.api.ProfileApi
import com.vangelnum.wisher.features.profile.data.repository.UpdateProfileRepositoryImpl
import com.vangelnum.wisher.features.profile.domain.repository.UpdateProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {
    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApi {
        return retrofit.create(ProfileApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        profileApi: ProfileApi,
        updateImageApi: UploadImageApi,
        errorParser: ErrorParser
    ): UpdateProfileRepository {
        return UpdateProfileRepositoryImpl(profileApi, updateImageApi, errorParser)
    }
}