package com.viroge.booksanalyzer.domain.usecase.book

import com.viroge.booksanalyzer.data.repository.BooksRepository
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.TempBook
import javax.inject.Inject

class SaveBookUseCase @Inject constructor(
    private val booksRepo: BooksRepository,
) {
    suspend operator fun invoke(book: TempBook): Result<SaveBookResult> {
        return runCatching {
            val res = booksRepo.insertFromBook(book)
            SaveBookResult(res.book)
        }
    }
}

data class SaveBookResult(
    val book: Book,
)
