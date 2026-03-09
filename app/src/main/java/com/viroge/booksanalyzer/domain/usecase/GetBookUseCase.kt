package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.domain.model.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBookUseCase @Inject constructor(
    private val repo: BooksRepository,
) {
    operator fun invoke(
        bookId: String,
    ): Flow<Book> {
        return repo.observeBook(bookId)
            .map { book ->
                // If the DB returns null, treat it as an error
                book ?: throw NoSuchElementException("Book with ID $bookId not found")
            }
    }
}
