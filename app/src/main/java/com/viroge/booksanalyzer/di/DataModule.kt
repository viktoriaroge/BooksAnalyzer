package com.viroge.booksanalyzer.di

import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.data.BooksRepositoryImpl
import com.viroge.booksanalyzer.data.SearchHistoryRepository
import com.viroge.booksanalyzer.data.SearchHistoryRepositoryImpl
import com.viroge.booksanalyzer.data.DeleteBooksScheduler
import com.viroge.booksanalyzer.data.DeleteBooksSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindBooksRepository(
        repo: BooksRepositoryImpl,
    ): BooksRepository

    @Binds
    @Singleton
    abstract fun bindSearchHistoryRepository(
        repo: SearchHistoryRepositoryImpl,
    ): SearchHistoryRepository

    @Binds
    abstract fun bindDeleteScheduler(
        impl: DeleteBooksSchedulerImpl,
    ): DeleteBooksScheduler
}
