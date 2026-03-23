package com.viroge.booksanalyzer.domain.usecase.selection

import com.viroge.booksanalyzer.data.repository.UserSelectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBookCoverUrlSelectionUseCase @Inject constructor(
    private val repository: UserSelectionRepository,
) {

    operator fun invoke(): Flow<String?> = repository.selectedCoverUrl
}
