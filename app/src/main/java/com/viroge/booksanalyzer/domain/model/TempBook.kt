package com.viroge.booksanalyzer.domain.model

data class TempBook(
    val source: BookSource,
    val sourceId: String?,
    val title: String,
    val authors: List<String>,
    val year: String?,
    val isbn13: String?,
    val isbn10: String?,
    val originalCoverUrl: String?,
    val coverUrl: String?,
)
