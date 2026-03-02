package com.viroge.booksanalyzer.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @OptIn(ExperimentalTime::class)
    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.System

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
}
