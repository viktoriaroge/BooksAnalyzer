package com.viroge.booksanalyzer.di

import com.viroge.booksanalyzer.data.SearchHistoryRepository
import com.viroge.booksanalyzer.data.SearchHistoryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchHistoryRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSearchHistoryRepository(
        repo: SearchHistoryRepositoryImpl,
    ): SearchHistoryRepository
}
