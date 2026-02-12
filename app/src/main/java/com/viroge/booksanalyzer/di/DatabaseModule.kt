package com.viroge.booksanalyzer.di

import android.content.Context
import androidx.room.Room
import com.viroge.booksanalyzer.data.local.books.BookDao
import com.viroge.booksanalyzer.data.local.BooksAnalyzerDb
import com.viroge.booksanalyzer.data.local.searchhistory.SearchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): BooksAnalyzerDb =
        Room.databaseBuilder(context, BooksAnalyzerDb::class.java, "books_analyzer.db")
            .fallbackToDestructiveMigration() // TODO: ok for MVP; remove later
            .build()

    @Provides
    fun provideBookDao(db: BooksAnalyzerDb): BookDao = db.bookDao()

    @Provides
    fun provideSearchHistoryDao(db: BooksAnalyzerDb): SearchHistoryDao = db.searchHistoryDao()
}
