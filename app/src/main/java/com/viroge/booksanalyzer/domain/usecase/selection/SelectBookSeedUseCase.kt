package com.viroge.booksanalyzer.domain.usecase.selection

import com.viroge.booksanalyzer.data.repository.UserSelectionRepository
import com.viroge.booksanalyzer.domain.model.BookSeed
import javax.inject.Inject

class SelectBookSeedUseCase @Inject constructor(
    private val repository: UserSelectionRepository,
) {
    suspend operator fun invoke(book: BookSeed?) = repository.updateBookSeed(book)
}
