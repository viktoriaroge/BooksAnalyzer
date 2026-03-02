package com.viroge.booksanalyzer.data

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.viroge.booksanalyzer.data.DeleteWork.UNIQUE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DeleteBooksSchedulerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : DeleteBooksScheduler {

    override fun enqueueBulkDelete() {
        Log.d("DeleteBooksSchedulerImpl", "enqueueBulkDelete called")

        val deleteRequest = OneTimeWorkRequestBuilder<DeleteItemsWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .addTag("cleanup_work")
            .build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork(
            uniqueWorkName = UNIQUE_NAME,
            existingWorkPolicy = ExistingWorkPolicy.KEEP,
            request = deleteRequest,
        )
    }
}

object DeleteWork {
    const val UNIQUE_NAME = "bulk_delete_pending_items"
}
