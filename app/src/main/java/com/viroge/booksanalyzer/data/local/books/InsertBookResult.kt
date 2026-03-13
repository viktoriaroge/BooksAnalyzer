package com.viroge.booksanalyzer.data.local.books

import com.viroge.booksanalyzer.domain.model.Book

data class InsertBookResult(
    val book: Book,
    val wasInserted: Boolean,
)
