package com.viroge.booksanalyzer.di

import com.viroge.booksanalyzer.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {

    @Provides
    @Singleton
    fun provideGoogleBooksApiKey(): String = BuildConfig.GOOGLE_BOOKS_API_KEY
}

