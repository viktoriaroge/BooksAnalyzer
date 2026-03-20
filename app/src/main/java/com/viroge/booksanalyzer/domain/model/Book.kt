package com.viroge.booksanalyzer.domain.model

data class Book(
    val id: String,
    val source: BookSource,
    val sourceId: String?,
    val status: ReadingStatus,
    val title: String,
    val authors: List<String>,
    val publishedYear: String? = null,
    val isbn13: String? = null,
    val isbn10: String? = null,
    val originalCoverUrl: String? = null,
    val coverUrl: String? = null,
    val createdAtEpochMs: Long,
    val lastOpenAtEpochMs: Long,
    val lastMarkedToDelete: Long,
    val toBeDeleted: Boolean,
)