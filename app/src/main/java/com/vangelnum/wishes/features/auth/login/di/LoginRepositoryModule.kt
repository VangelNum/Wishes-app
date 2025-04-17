package com.vangelnum.wishes.features.auth.login.di

import com.vangelnum.wishes.features.auth.login.data.repository.LoginRepositoryImpl
import com.vangelnum.wishes.features.auth.login.domain.repository.LoginRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoginRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindLoginRepository(loginRepositoryImpl: LoginRepositoryImpl): LoginRepository
}