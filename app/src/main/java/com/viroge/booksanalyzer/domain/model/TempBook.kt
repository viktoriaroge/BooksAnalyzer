package com.viroge.booksanalyzer.domain.model

data class TempBook(
    val source: BookSource,
    val sourceId: String?,
    val title: String,
    val authors: List<String>,
    val year: String? = null,
    val isbn13: String? = null,
    val isbn10: String? = null,
    val coverUrl: String? = null,
)