package com.viroge.booksanalyzer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.viroge.booksanalyzer.data.local.books.BookDao
import com.viroge.booksanalyzer.data.local.books.BookEntity
import com.viroge.booksanalyzer.data.local.searchhistory.SearchHistoryDao
import com.viroge.booksanalyzer.data.local.searchhistory.SearchHistoryEntity

@Database(
    entities = [BookEntity::class, SearchHistoryEntity::class],
    version = 8,
    exportSchema = true,
)
abstract class BooksAnalyzerDb : RoomDatabase() {

    abstract fun bookDao(): BookDao

    abstract fun searchHistoryDao(): SearchHistoryDao
}
