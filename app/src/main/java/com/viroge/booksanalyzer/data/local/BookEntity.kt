package com.viroge.booksanalyzer.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    indices = [
        Index(value = ["isbn13"], unique = true),
        Index(value = ["isbn10"], unique = true),
        Index(value = ["googleVolumeId"], unique = true),
        Index(value = ["openLibraryId"], unique = true),
        Index(value = ["titleKey"], unique = true)
    ]
)
data class BookEntity(
    @PrimaryKey val bookId: String,     // UUID string

    val title: String,
    val authors: String,                // comma-separated for MVP

    val titleKey: String,               // normalized identity key

    val publishedYear: Int? = null,
    val isbn13: String? = null,
    val isbn10: String? = null,
    val openLibraryId: String? = null,
    val googleVolumeId: String? = null,

    val coverUrl: String? = null,

    val status: String,                 // store enum as String for now (easy migrations)
    val createdAtEpochMs: Long,
)
