package com.viroge.booksanalyzer.domain.delete

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.viroge.booksanalyzer.data.BooksRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException

@HiltWorker
class DeleteItemsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val booksRepo: BooksRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("DeleteItemsWorker", "doWork called")

            // Attempt to find and delete books marked for deletion for longer than a week:
            val list = booksRepo.getPendingDeleteBooks()

            val now = System.currentTimeMillis()
            val expired = list.filter {
                val sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L
                // Leave it in the list if expired:
                (now - it.lastMarkedToDelete) > sevenDaysInMillis
            }

            Log.d("DeleteItemsWorker", "doWork will attempt to delete ${expired.size} books")
            for (book in expired) {
                booksRepo.deleteBook(book.id)
            }

            Log.d("DeleteItemsWorker", "doWork success")
            Result.success()

        } catch (e: IOException) {
            Log.d("DeleteItemsWorker", "doWork retry")
            Result.retry() // transient network/storage errors
        } catch (e: Exception) {
            Log.d("DeleteItemsWorker", "doWork failure")
            Result.failure() // unexpected, likely not recoverable
        }
    }
}
