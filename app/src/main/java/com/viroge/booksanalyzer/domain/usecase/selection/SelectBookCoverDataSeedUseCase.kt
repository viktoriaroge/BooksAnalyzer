package com.viroge.booksanalyzer.domain.usecase.selection

import com.viroge.booksanalyzer.data.repository.UserSelectionRepository
import com.viroge.booksanalyzer.domain.model.BookCoverDataSeed
import javax.inject.Inject

class SelectBookCoverDataSeedUseCase @Inject constructor(
    private val repository: UserSelectionRepository,
) {
    suspend operator fun invoke(seed: BookCoverDataSeed?) = repository.updateBookCoverSeed(seed)
}
