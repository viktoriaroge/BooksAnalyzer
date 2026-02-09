package com.viroge.booksanalyzer.ui.books.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.data.local.BookEntity
import com.viroge.booksanalyzer.domain.ReadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookDetailsUiState(
    val book: BookEntity? = null,
    val isDeleting: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val repo: BooksRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val bookId: String = checkNotNull(savedStateHandle["bookId"])

    private val _ui = MutableStateFlow(BookDetailsUiState())
    val ui: StateFlow<BookDetailsUiState> = _ui.asStateFlow()

    init {
        repo.observeBook(bookId)
            .onEach { book -> _ui.update { it.copy(book = book, error = null) } }
            .catch { e -> _ui.update { it.copy(error = e.message ?: "Failed to load book") } }
            .launchIn(viewModelScope)
    }

    fun setStatus(
        status: ReadingStatus,
    ) {
        viewModelScope.launch {
            runCatching { repo.updateStatus(bookId, status) }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            error = e.message ?: "Failed to update status"
                        )
                    }
                }
        }
    }

    fun delete(
        onDeleted: () -> Unit,
    ) {
        viewModelScope.launch {
            _ui.update { it.copy(isDeleting = true, error = null) }
            runCatching { repo.deleteBook(bookId) }
                .onSuccess { onDeleted() }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            isDeleting = false,
                            error = e.message ?: "Failed to delete"
                        )
                    }
                }
        }
    }
}
