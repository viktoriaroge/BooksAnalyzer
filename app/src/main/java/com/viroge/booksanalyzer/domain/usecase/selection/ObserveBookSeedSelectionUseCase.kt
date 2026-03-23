package com.viroge.booksanalyzer.domain.usecase.selection

import com.viroge.booksanalyzer.data.repository.UserSelectionRepository
import com.viroge.booksanalyzer.domain.model.BookSeed
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBookSeedSelectionUseCase @Inject constructor(
    private val repository: UserSelectionRepository,
) {

    operator fun invoke(): Flow<BookSeed?> = repository.selectedBookSeed
}
