package com.vangelnum.wisher.features.auth.register.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vangelnum.wisher.core.utils.ErrorParser
import com.vangelnum.wisher.features.auth.register.data.api.RegisterApi
import com.vangelnum.wisher.features.auth.register.data.repository.RegisterRepositoryImpl
import com.vangelnum.wisher.features.auth.register.domain.repository.RegisterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RegisterModule {
    @Provides
    @Singleton
    fun provideRegisterApi(retrofit: Retrofit): RegisterApi {
        return retrofit.create(RegisterApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRegisterRepository(
        api: RegisterApi,
        dataStore: DataStore<Preferences>,
        errorParser: ErrorParser
    ): RegisterRepository {
        return RegisterRepositoryImpl(api, dataStore, errorParser)
    }
}