package com.empire_mammoth.pixelbloom.di

import com.empire_mammoth.pixelbloom.data.repositories.GenerateImageRepositoryImpl
import com.empire_mammoth.pixelbloom.data.repositories.SaveRepositoryImpl
import com.empire_mammoth.pixelbloom.domain.repositories.GenerateImageRepository
import com.empire_mammoth.pixelbloom.domain.repositories.SaveRepository
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
    abstract fun bindGenerateImageRepository(
        firebaseAnalytics: GenerateImageRepositoryImpl
    ): GenerateImageRepository

    @Binds
    @Singleton
    abstract fun bindSaveRepository(
        firebaseAnalytics: SaveRepositoryImpl
    ): SaveRepository
}