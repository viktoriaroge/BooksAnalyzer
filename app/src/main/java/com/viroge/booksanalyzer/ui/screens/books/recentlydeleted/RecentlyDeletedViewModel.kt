package com.viroge.booksanalyzer.ui.screens.books.recentlydeleted

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.domain.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentlyDeletedViewModel @Inject constructor(
    private val repo: BooksRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RecentlyDeletedUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching {
                val list = repo.getPendingDeleteBooks()

                val now = System.currentTimeMillis()
                val notExpired = list.filter {
                    val sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L
                    // Leave it in the list if still not expired:
                    (now - it.lastMarkedToDelete) <= sevenDaysInMillis
                }
                _state.update { it.copy(books = notExpired) }

            }.onFailure { e ->
                _state.update { it.copy(error = e.message ?: "Failed to fetch Books pending deletion") }
            }
        }

        fun restoreBook(bookId: String) {
            viewModelScope.launch {
                runCatching { repo.restoreBookMarkedToDelete(bookId) }
                    .onFailure { e ->
                        _state.update { it.copy(error = e.message ?: "Failed to restore book") }
                    }
            }
        }
    }
}

data class RecentlyDeletedUiState(
    val books: List<Book> = emptyList(),
    val error: String? = null,
)
