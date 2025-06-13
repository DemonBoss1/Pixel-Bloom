package com.empire_mammoth.pixelbloom.di

import com.empire_mammoth.pixelbloom.data.repositories.GenerateImageRepositoryImpl
import com.empire_mammoth.pixelbloom.domain.repositories.GenerateImageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsService(
        firebaseAnalytics: GenerateImageRepositoryImpl
    ): GenerateImageRepository
}