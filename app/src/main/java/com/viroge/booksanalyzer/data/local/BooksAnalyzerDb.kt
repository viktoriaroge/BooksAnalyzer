package com.viroge.booksanalyzer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BookEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class BooksAnalyzerDb : RoomDatabase() {

    abstract fun bookDao(): BookDao
}
