package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.domain.BookMapper
import com.viroge.booksanalyzer.domain.model.Book
import javax.inject.Inject

class ValidateAndGetManualBookUseCase @Inject constructor(
    private val bookMapper: BookMapper,
) {
    operator fun invoke(
        title: String,
        authors: String,
        year: String?,
        isbn13: String?,
    ): Result<Book> {

        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) {
            return Result.failure(Exception("Title is required"))
        }

        return Result.success(
            bookMapper.mapFromManualInput(
                title = trimmedTitle,
                authors = authors,
                year = year,
                isbn13 = isbn13,
            )
        )
    }
}
