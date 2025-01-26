package com.vangelnum.wisher.features.auth.login.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vangelnum.wisher.features.auth.login.data.api.LoginApi
import com.vangelnum.wisher.features.auth.login.data.repository.LoginRepositoryImpl
import com.vangelnum.wisher.features.auth.login.domain.repository.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {
    @Provides
    @Singleton
    fun provideLoginApi(retrofit: Retrofit): LoginApi {
        return retrofit.create(LoginApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginRepository(api: LoginApi, dataStore: DataStore<Preferences>): LoginRepository {
        return LoginRepositoryImpl(api, dataStore)
    }
}