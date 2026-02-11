package com.viroge.booksanalyzer.domain

data class BooksPageResult(
    val items: List<BookCandidate>,
    val errors: List<Throwable>,
    val nextToken: String?, // null => no more
)

data class PageToken(
    val googleStart: Int,
    val olPage: Int,
)
