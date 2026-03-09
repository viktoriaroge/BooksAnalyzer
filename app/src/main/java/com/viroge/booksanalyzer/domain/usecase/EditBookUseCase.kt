package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.domain.model.Book
import javax.inject.Inject

class EditBookUseCase @Inject constructor(
    private val booksRepo: BooksRepository,
) {
    suspend operator fun invoke(book: Book): Result<EditBookResult> {
        return runCatching {
            val res = booksRepo.insertFromBook(book, wasEdited = true)
            EditBookResult(res.bookId)
        }
    }
}

data class EditBookResult(
    val bookId: String,
)
