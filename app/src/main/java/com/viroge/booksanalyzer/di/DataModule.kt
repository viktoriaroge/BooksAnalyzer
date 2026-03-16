package com.viroge.booksanalyzer.di

import com.viroge.booksanalyzer.data.repository.BooksRepository
import com.viroge.booksanalyzer.data.repository.BooksRepositoryImpl
import com.viroge.booksanalyzer.data.repository.search.SearchHistoryRepository
import com.viroge.booksanalyzer.data.repository.search.SearchHistoryRepositoryImpl
import com.viroge.booksanalyzer.data.sync.book.DeleteBooksScheduler
import com.viroge.booksanalyzer.data.sync.book.DeleteBooksSchedulerImpl
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
