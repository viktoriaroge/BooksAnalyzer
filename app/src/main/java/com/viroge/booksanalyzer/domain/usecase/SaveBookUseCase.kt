package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.domain.model.Book
import javax.inject.Inject

class SaveBookUseCase @Inject constructor(
    private val booksRepo: BooksRepository,
) {
    suspend operator fun invoke(book: Book): Result<SaveBookResult> {
        return runCatching {
            val res = booksRepo.insertFromBook(book)
            SaveBookResult(res.bookId)
        }
    }
}

data class SaveBookResult(
    val bookId: String,
)
