package com.viroge.booksanalyzer.data.local.books

import androidx.room.ColumnInfo
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
        Index(value = ["titleKey"], unique = true),
    ]
)
data class BookEntity(
    @PrimaryKey
    @ColumnInfo(name = "bookId")
    val bookId: String, // UUID string

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "authors")
    val authors: String, // comma-separated for MVP

    @ColumnInfo(name = "titleKey")
    val titleKey: String, // normalized identity key

    @ColumnInfo(name = "publishedYear")
    val publishedYear: Int? = null,

    @ColumnInfo(name = "isbn13")
    val isbn13: String? = null,

    @ColumnInfo(name = "isbn10")
    val isbn10: String? = null,

    @ColumnInfo(name = "openLibraryId")
    val openLibraryId: String? = null,

    @ColumnInfo(name = "googleVolumeId")
    val googleVolumeId: String? = null,

    @ColumnInfo(name = "coverUrl")
    val coverUrl: String? = null,

    @ColumnInfo(name = "status")
    val status: String, // store enum as String for now (easy migrations)

    @ColumnInfo(name = "createdAtEpochMs")
    val createdAtEpochMs: Long,

    @ColumnInfo(name = "lastOpenAtEpochMs")
    val lastOpenAtEpochMs: Long,
)
