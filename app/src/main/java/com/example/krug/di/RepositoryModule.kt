package com.example.krug.di

import com.example.krug.data.repository.AuthRepository
import com.example.krug.data.repository.EventRepository
import com.example.krug.data.repository.PlanningRepository
import com.example.krug.data.repository.RetrofitAuthRepository
import com.example.krug.data.repository.RetrofitEventRepository
import com.example.krug.data.repository.RetrofitPlanningRepository
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
        repo: RetrofitAuthRepository
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindEventRepository(
        repo: RetrofitEventRepository
    ): EventRepository

    @Binds
    @Singleton
    abstract fun bindPlanningRepository(repo: RetrofitPlanningRepository): PlanningRepository
}