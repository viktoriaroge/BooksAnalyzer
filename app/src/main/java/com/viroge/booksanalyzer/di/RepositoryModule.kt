package com.viroge.booksanalyzer.di

import com.viroge.booksanalyzer.data.BookSearchRepository
import com.viroge.booksanalyzer.data.BookSearchRepositoryImpl
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
    abstract fun bindBookSearchRepository(
        impl: BookSearchRepositoryImpl,
    ): BookSearchRepository
}
