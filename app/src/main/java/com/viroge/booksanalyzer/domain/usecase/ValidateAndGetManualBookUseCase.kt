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
        publishedYear: Int?,
        isbn13: String?,
        coverUrl: String?
    ): Result<Book> {

        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) {
            return Result.failure(Exception("Title is required"))
        }

        return Result.success(
            bookMapper.mapFromManualInput(
                title = trimmedTitle,
                authors = authors,
                publishedYear = publishedYear,
                isbn13 = isbn13,
                coverUrl = coverUrl
            )
        )
    }
}
