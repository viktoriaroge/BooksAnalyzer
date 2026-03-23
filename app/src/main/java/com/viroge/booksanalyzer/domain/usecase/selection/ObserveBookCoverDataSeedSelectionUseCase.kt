package com.viroge.booksanalyzer.domain.usecase.selection

import com.viroge.booksanalyzer.data.repository.UserSelectionRepository
import com.viroge.booksanalyzer.domain.model.BookCoverDataSeed
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBookCoverDataSeedSelectionUseCase @Inject constructor(
    private val repository: UserSelectionRepository,
) {

    operator fun invoke(): Flow<BookCoverDataSeed?> = repository.selectedBookCoverDataSeed
}
