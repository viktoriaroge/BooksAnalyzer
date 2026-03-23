package com.viroge.booksanalyzer.domain.usecase.selection

import com.viroge.booksanalyzer.data.repository.UserSelectionRepository
import javax.inject.Inject

class SelectBookCoverUrlUseCase @Inject constructor(
    private val repository: UserSelectionRepository,
) {
    suspend operator fun invoke(url: String?) = repository.updateCoverUrl(url)
}
