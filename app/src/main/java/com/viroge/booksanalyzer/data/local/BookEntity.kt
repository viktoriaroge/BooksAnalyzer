package com.viroge.booksanalyzer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val bookId: String,      // UUID string
    val title: String,
    val authors: String,                 // comma-separated for MVP
    val publishedYear: Int? = null,

    val isbn13: String? = null,
    val isbn10: String? = null,
    val openLibraryId: String? = null,
    val googleVolumeId: String? = null,

    val coverUrl: String? = null,

    val status: String,                  // store enum as String for now (easy migrations)
    val createdAtEpochMs: Long,
)
