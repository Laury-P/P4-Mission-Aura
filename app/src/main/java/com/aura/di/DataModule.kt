package com.aura.di

import com.aura.data.network.APIService
import com.aura.data.repository.AuraRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun providesAuraRepository(apiService: APIService): AuraRepository {
        return AuraRepository(apiService)
    }
}