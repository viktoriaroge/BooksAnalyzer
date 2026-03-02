package com.viroge.booksanalyzer.domain.model.library

import com.viroge.booksanalyzer.domain.model.ReadingStatus

data class LibraryFilters(
    val status: ReadingStatus? = null,     // null = All
    val sort: LibrarySort = LibrarySort.RECENT,
)