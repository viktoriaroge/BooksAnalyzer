package com.viroge.booksanalyzer.domain.usecase.selection

import com.viroge.booksanalyzer.data.repository.UserSelectionRepository
import com.viroge.booksanalyzer.domain.model.TempBook
import javax.inject.Inject

class SelectTempBookUseCase @Inject constructor(
    private val repository: UserSelectionRepository,
) {
    suspend operator fun invoke(book: TempBook?) = repository.updateTempBook(book)
}
