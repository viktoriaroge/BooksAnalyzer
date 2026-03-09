package com.viroge.booksanalyzer.domain.usecase

import android.util.Log
import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import javax.inject.Inject

class UpdateBookStatusUseCase @Inject constructor(
    private val booksRepo: BooksRepository,
) {
    suspend operator fun invoke(bookId: String, status: ReadingStatus): Result<Unit> {
        return runCatching {
            booksRepo.updateStatus(bookId, status)
        }.onFailure {
            Log.e("UpdateBookStatus", "Failed to update status for $bookId", it)
        }
    }
}
