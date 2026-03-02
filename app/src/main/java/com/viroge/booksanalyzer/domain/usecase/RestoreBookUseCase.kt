package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.data.BooksRepository
import javax.inject.Inject

class RestoreBookUseCase @Inject constructor(
    private val repo: BooksRepository,
) {

    suspend operator fun invoke(bookId: String): Result<Unit> = runCatching {
        repo.restoreBookMarkedToDelete(bookId)
    }
}
