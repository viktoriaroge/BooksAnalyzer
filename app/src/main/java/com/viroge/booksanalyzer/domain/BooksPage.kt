package com.viroge.booksanalyzer.domain

import com.viroge.booksanalyzer.domain.model.TempBook

data class BooksPage(
    val items: List<TempBook>,
    val errors: List<Throwable>,
    val nextToken: String?, // null => no more
)

data class PageToken(
    val googleStart: Int,
    val olPage: Int,
)
