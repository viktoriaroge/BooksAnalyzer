package com.viroge.booksanalyzer.domain

data class BookCandidate(
    val source: Source,
    val sourceId: String,
    val title: String,
    val authors: List<String>,
    val publishedYear: Int? = null,
    val isbn13: String? = null,
    val isbn10: String? = null,
    val coverUrl: String? = null
) {
    enum class Source { GOOGLE_BOOKS, OPEN_LIBRARY, MANUAL }
}
