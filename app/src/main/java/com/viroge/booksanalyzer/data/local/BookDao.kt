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

    @Query("SELECT * FROM books WHERE isbn13 = :isbn13 LIMIT 1")
    suspend fun findByIsbn13(
        isbn13: String,
    ): BookEntity?

    @Query("SELECT * FROM books WHERE isbn10 = :isbn10 LIMIT 1")
    suspend fun findByIsbn10(
        isbn10: String,
    ): BookEntity?

    @Query("SELECT * FROM books WHERE googleVolumeId = :googleId LIMIT 1")
    suspend fun findByGoogleId(
        googleId: String,
    ): BookEntity?

    @Query("SELECT * FROM books WHERE openLibraryId = :olId LIMIT 1")
    suspend fun findByOpenLibraryId(
        olId: String,
    ): BookEntity?

    @Query("SELECT * FROM books WHERE titleKey = :titleKey LIMIT 1")
    suspend fun findByTitleKey(
        titleKey: String,
    ): BookEntity?

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
