package com.viroge.booksanalyzer.ui.screens.books

object BookTransitionKey {

    fun calculate(title: String, authors: List<String>, isbn: String?): String {
        val normalizedAuthors = authors.joinToString(separator = ", ")
        return calculate(title, normalizedAuthors, isbn)
    }

    fun calculate(title: String, authors: String?, isbn: String?): String {
        val normalizedIsbn = isbn?.trim()?.lowercase() ?: "no-isbn"
        val normalizedTitle = title.trim().lowercase().hashCode()
        val normalizedAuthors = authors?.trim()?.lowercase()?.hashCode() ?: "no-authors"

        // Example output: "book-9780547928227-1234567-890123"
        return "book-$normalizedIsbn-$normalizedTitle-$normalizedAuthors"
    }
}
