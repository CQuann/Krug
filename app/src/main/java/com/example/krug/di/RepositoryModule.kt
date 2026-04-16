package com.example.krug.di

import com.example.krug.data.repository.AuthRepository
import com.example.krug.data.repository.RetrofitAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        retrofitAuthRepository: RetrofitAuthRepository
    ): AuthRepository
}