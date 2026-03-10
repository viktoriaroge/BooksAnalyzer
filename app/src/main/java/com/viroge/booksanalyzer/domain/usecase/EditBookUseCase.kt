package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.data.BooksRepository
import javax.inject.Inject

class EditBookUseCase @Inject constructor(
    private val booksRepo: BooksRepository,
) {
    suspend operator fun invoke(
        bookId: String,
        title: String,
        authors: String,
        year: String?,
        isbn13: String?,
        isbn10: String?,
        coverUrl: String?,
    ): Result<EditBookResult> {
        return runCatching {
            booksRepo.editBook(
                bookId = bookId,
                title = title,
                authors = authors,
                year = year,
                isbn13 = isbn13,
                isbn10 = isbn10,
                coverUrl = coverUrl,
            )
            EditBookResult(bookId)
        }
    }
}

data class EditBookResult(
    val bookId: String,
)
