package com.viroge.booksanalyzer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(
        book: BookEntity,
    )

    @Query("SELECT * FROM books ORDER BY createdAtEpochMs DESC")
    fun observeAll(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE bookId = :bookId LIMIT 1")
    fun observeById(
        bookId: String,
    ): Flow<BookEntity?>

    @Query("SELECT * FROM books WHERE bookId = :bookId LIMIT 1")
    suspend fun getById(
        bookId: String,
    ): BookEntity?

    @Query("DELETE FROM books WHERE bookId = :bookId")
    suspend fun deleteById(
        bookId: String,
    )
}
