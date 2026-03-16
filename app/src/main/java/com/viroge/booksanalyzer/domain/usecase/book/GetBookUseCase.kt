package com.viroge.booksanalyzer.domain.usecase.book

import com.viroge.booksanalyzer.data.repository.BooksRepository
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
