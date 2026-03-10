package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.domain.model.Book
import javax.inject.Inject

class GetBookUseCase @Inject constructor(
    private val repo: BooksRepository,
) {
    suspend operator fun invoke(
        bookId: String,
    ): Book? {
        return repo.getBook(bookId)
    }
}
