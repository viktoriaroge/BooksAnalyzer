package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.domain.Book
import com.viroge.booksanalyzer.domain.LibraryFilters
import com.viroge.booksanalyzer.domain.LibrarySort
import com.viroge.booksanalyzer.domain.ReadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    booksRepo: BooksRepository,
) : ViewModel() {

    private val allBooks: Flow<List<Book>> = booksRepo.observeLibrary()
    private val query = MutableStateFlow(value = "")
    private val statusFilter = MutableStateFlow<ReadingStatus?>(value = null) // null == All
    private val sort = MutableStateFlow(value = LibrarySort.ADDED)

    val filters: StateFlow<LibraryFilters> =
        combine(statusFilter, sort) { status, sort ->
            LibraryFilters(status, sort)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = LibraryFilters()
        )

    val uiState: StateFlow<LibraryUiState> =
        combine(allBooks, query, statusFilter, sort) { books, q, status, sort ->
            val qq = q.trim().lowercase()

            var filtered = books

            if (status != null) {
                filtered = filtered.filter { it.status == status }
            }

            if (qq.isNotBlank()) {
                filtered = filtered.filter {
                    it.title.lowercase().contains(other = qq) ||
                            it.authors.joinToString(separator = ", ").lowercase().contains(other = qq) ||
                            (it.isbn13?.contains(other = qq) == true) ||
                            (it.isbn10?.contains(other = qq) == true)
                }
            }

            val sorted = when (sort) {
                LibrarySort.ADDED -> filtered.sortedByDescending { it.createdAtEpochMs }
                LibrarySort.RECENT -> filtered.sortedByDescending { it.lastOpenAtEpochMs }
                LibrarySort.TITLE -> filtered.sortedBy { it.title.lowercase() }
                LibrarySort.AUTHOR -> filtered.sortedBy { it.authors.getOrNull(0)?.lowercase() }
            }

            val currentlyReading = filtered
                .sortedByDescending { it.lastOpenAtEpochMs } // always by most recent
                .filter { it.status == ReadingStatus.READING }
                .take(n = 5) // max 5 books

            LibraryUiState(
                query = q,
                selectedStatus = status,
                sort = sort,
                currentlyReading = currentlyReading,
                books = sorted,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = LibraryUiState()
        )

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onStatusChange(status: ReadingStatus?) {
        statusFilter.value = status
    }

    fun onSortChange(newSort: LibrarySort) {
        sort.value = newSort
    }

    fun onClearFilters() {
        statusFilter.value = null
        sort.value = LibrarySort.ADDED
    }
}

data class LibraryUiState(
    val query: String = "",
    val selectedStatus: ReadingStatus? = null,
    val sort: LibrarySort = LibrarySort.ADDED,
    val currentlyReading: List<Book> = emptyList(),
    val books: List<Book> = emptyList(),
)
