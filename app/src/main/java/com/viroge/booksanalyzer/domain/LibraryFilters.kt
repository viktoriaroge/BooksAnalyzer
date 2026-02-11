package com.viroge.booksanalyzer.domain

data class LibraryFilters(
    val status: ReadingStatus? = null,     // null = All
    val sort: LibrarySort = LibrarySort.RECENT,
)
