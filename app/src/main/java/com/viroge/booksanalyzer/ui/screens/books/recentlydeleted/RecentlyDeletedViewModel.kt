package com.viroge.booksanalyzer.ui.screens.books.recentlydeleted

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.domain.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentlyDeletedViewModel @Inject constructor(
    private val repo: BooksRepository,
) : ViewModel() {

    private val books: Flow<List<Book>> = repo.observePendingDeleteBooks()

    private val _message = MutableSharedFlow<String?>()
    val message: SharedFlow<String?> = _message

    val state: StateFlow<RecentlyDeletedUiState> = books
        .map { list ->
            val now = System.currentTimeMillis()
            val notExpiredList = list.filter {
                val sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L
                // Leave it in the list if still not expired:
                (now - it.lastMarkedToDelete) <= sevenDaysInMillis
            }
            RecentlyDeletedUiState(books = notExpiredList)
        }
        .distinctUntilChanged()
        .catch { _ -> _message.emit("Failed to retrieve Books pending deletion.") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = RecentlyDeletedUiState()
        )

    fun restoreBook(bookId: String) {
        viewModelScope.launch {
            runCatching { repo.restoreBookMarkedToDelete(bookId) }
                .onFailure { _ -> _message.emit("Failed to restore book.") }
        }
    }
}

data class RecentlyDeletedUiState(
    val books: List<Book> = emptyList(),
)
