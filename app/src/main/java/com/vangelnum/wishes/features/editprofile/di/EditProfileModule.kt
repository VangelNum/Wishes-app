package com.vangelnum.wishes.features.editprofile.di

import com.vangelnum.wishes.features.editprofile.data.api.EditProfileApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EditProfileModule {
    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): EditProfileApi {
        return retrofit.create(EditProfileApi::class.java)
    }
}