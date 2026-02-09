package com.viroge.booksanalyzer.di

import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.data.BooksRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BooksRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBooksRepository(
        impl: BooksRepositoryImpl,
    ): BooksRepository
}
