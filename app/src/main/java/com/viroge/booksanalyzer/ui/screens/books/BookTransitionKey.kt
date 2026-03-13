package com.viroge.booksanalyzer.ui.screens.books

import com.viroge.booksanalyzer.domain.model.BookSource

object BookTransitionKey {

    fun calculate(
        title: String,
        authors: List<String>,
        isbn: String?,
        source: BookSource,
        sourceId: String?,
    ): String {
        val normalizedAuthors = authors.joinToString(separator = ", ")
        return calculate(title, normalizedAuthors, isbn, source, sourceId)
    }

    fun calculate(
        title: String,
        authors: String?,
        isbn: String?,
        source: BookSource,
        sourceId: String?,
    ): String {
        val normalizedIsbn = isbn?.trim()?.lowercase() ?: "no-isbn"
        val normalizedTitle = title.trim().lowercase().hashCode()
        val normalizedAuthors = authors?.trim()?.lowercase()?.hashCode() ?: "no-authors"
        val normalizedSource = source.name.lowercase().hashCode()
        val normalizedSourceId = sourceId?.trim()?.lowercase()?.hashCode() ?: "no-source-id"

        // Example output: "book-9780547928227-1234567-890123-2345678-7890123"
        return "book-$normalizedIsbn-$normalizedTitle-$normalizedAuthors-$normalizedSource-$normalizedSourceId"
    }
}
