package com.viroge.booksanalyzer.domain.usecase.book

import com.viroge.booksanalyzer.data.repository.BooksRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class MarkBookAsOpenedUseCase @Inject constructor(
    private val booksRepo: BooksRepository,
) {
    suspend operator fun invoke(bookId: String) {
        // Business rule: wait for transitions/animations to settle
        delay(450)
        booksRepo.updateOnOpen(bookId)
    }
}