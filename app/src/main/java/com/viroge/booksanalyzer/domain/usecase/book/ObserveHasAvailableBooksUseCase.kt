package com.viroge.booksanalyzer.domain.usecase.book

import com.viroge.booksanalyzer.data.repository.BooksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHasAvailableBooksUseCase @Inject constructor(
    private val booksRepo: BooksRepository,
) {
    operator fun invoke(): Flow<Boolean> = booksRepo.observeHasAvailableBooks()
}
