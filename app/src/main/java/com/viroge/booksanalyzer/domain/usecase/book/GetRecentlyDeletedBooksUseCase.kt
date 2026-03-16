package com.viroge.booksanalyzer.domain.usecase.book

import com.viroge.booksanalyzer.data.repository.BooksRepository
import com.viroge.booksanalyzer.domain.model.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class GetRecentlyDeletedBooksUseCase @Inject constructor(
    private val repo: BooksRepository,
    private val clock: Clock,
) {

    operator fun invoke(): Flow<List<Book>> = repo.observePendingDeleteBooks().map { list ->
        val now = clock.now()

        list.filter { book ->
            val deletedAt = Instant.fromEpochMilliseconds(book.lastMarkedToDelete)

            (now - deletedAt) <= 7.days
        }
    }
}
