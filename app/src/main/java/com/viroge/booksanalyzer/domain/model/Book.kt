package com.viroge.booksanalyzer.domain.model

data class Book(
    val id: String,
    val source: BookSource,
    val sourceId: String?,
    val status: ReadingStatus,
    val title: String,
    val authors: List<String>,
    val publishedYear: String?,
    val isbn13: String?,
    val isbn10: String?,
    val originalCoverUrl: String?,
    val coverUrl: String?,
    val createdAtEpochMs: Long,
    val lastOpenAtEpochMs: Long,
    val lastMarkedToDelete: Long,
    val toBeDeleted: Boolean,
)