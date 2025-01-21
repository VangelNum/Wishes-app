package com.vangelnum.wisher.features.auth.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vangelnum.wisher.features.auth.api.AuthApi
import com.vangelnum.wisher.features.auth.data.repository.UserRepositoryImpl
import com.vangelnum.wisher.features.auth.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(api: AuthApi, dataStore: DataStore<Preferences>): UserRepository {
        return UserRepositoryImpl(api, dataStore)
    }
}