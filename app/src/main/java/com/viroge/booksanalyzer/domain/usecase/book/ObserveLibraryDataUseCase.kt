package com.viroge.booksanalyzer.domain.usecase.book

import com.viroge.booksanalyzer.data.repository.BooksRepository
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ObserveLibraryDataUseCase @Inject constructor(
    private val booksRepo: BooksRepository,
) {

    operator fun invoke(
        query: String,
        status: ReadingStatus?,
        sort: LibrarySort,
    ): Flow<LibraryData> {
        return booksRepo.observeLibrary().map { allBooks ->
            withContext(Dispatchers.Default) {
                val qq = query.trim().lowercase()

                val filtered = allBooks.filter { book ->
                    val matchesStatus = status == null || book.status == status
                    val matchesQuery = qq.isBlank() ||
                            book.title.lowercase().contains(qq) ||
                            book.authors.any { it.lowercase().contains(qq) } ||
                            book.isbn13?.contains(qq) == true ||
                            book.isbn10?.contains(qq) == true

                    matchesStatus && matchesQuery
                }

                val sorted = when (sort) {
                    LibrarySort.ADDED -> filtered.sortedByDescending { it.createdAtEpochMs }
                    LibrarySort.RECENT -> filtered.sortedByDescending { it.lastOpenAtEpochMs }
                    LibrarySort.TITLE -> filtered.sortedBy { it.title.lowercase() }
                    LibrarySort.AUTHOR -> filtered.sortedBy { it.authors.firstOrNull()?.lowercase() ?: "" }
                }

                val currentlyReading = filtered
                    .filter { it.status == ReadingStatus.READING }
                    .sortedByDescending { it.lastOpenAtEpochMs }
                    .take(5)

                LibraryData(
                    books = sorted,
                    currentlyReading = currentlyReading
                )
            }
        }
    }
}

data class LibraryData(
    val books: List<Book>,
    val currentlyReading: List<Book>
)

enum class LibrarySort {
    ADDED,
    RECENT,
    TITLE,
    AUTHOR,
}
