package com.viroge.booksanalyzer.data.remote.openlibrary

import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.TempBook
import com.viroge.booksanalyzer.ui.screens.books.BookTransitionKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenLibraryMapper @Inject constructor() {

    fun mapOrNull(doc: OpenLibraryDoc, coverUrl: String?): TempBook? {
        val title = doc.title?.takeIf { it.isNotBlank() } ?: return null
        val sourceId = doc.key ?: return null

        val (isbn13, isbn10) = splitIsbns(isbns = doc.isbn)

        val authors = doc.authorName
        val source = BookSource.OPEN_LIBRARY

        return TempBook(
            animationKey = BookTransitionKey.calculate(title, authors, isbn13, source, sourceId),
            sourceId = sourceId,
            source = source,
            title = title,
            authors = authors,
            year = doc.firstPublishYear?.toString(),
            isbn13 = isbn13,
            isbn10 = isbn10,
            originalCoverUrl = coverUrl,
            coverUrl = coverUrl,
        )
    }

    private fun splitIsbns(
        isbns: List<String>,
    ): Pair<String?, String?> {
        val isbn13 = isbns.firstOrNull { it.length == 13 }
        val isbn10 = isbns.firstOrNull { it.length == 10 }
        return isbn13 to isbn10
    }
}
