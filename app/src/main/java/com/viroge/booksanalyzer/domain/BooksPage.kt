package com.viroge.booksanalyzer.domain

import com.viroge.booksanalyzer.domain.model.Book

data class BooksPage(
    val items: List<Book>,
    val errors: List<Throwable>,
    val nextToken: String?, // null => no more
)

data class PageToken(
    val googleStart: Int,
    val olPage: Int,
)
