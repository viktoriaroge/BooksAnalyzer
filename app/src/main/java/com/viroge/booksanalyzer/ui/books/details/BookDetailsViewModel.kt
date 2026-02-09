package com.viroge.booksanalyzer.ui.books.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.data.local.BookEntity
import com.viroge.booksanalyzer.domain.ReadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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

sealed interface BookDetailEvent {

    data class Deleted(
        val title: String,
    ) : BookDetailEvent
}

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val repo: BooksRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val bookId: String = checkNotNull(savedStateHandle["bookId"])

    private val _ui = MutableStateFlow(value = BookDetailsUiState())
    val ui: StateFlow<BookDetailsUiState> = _ui.asStateFlow()

    private val _events = MutableSharedFlow<BookDetailEvent>()
    val events: SharedFlow<BookDetailEvent> = _events

    private var lastDeleted: BookEntity? = null

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

    fun delete() {
        viewModelScope.launch {
            _ui.update { it.copy(isDeleting = true, error = null) }

            runCatching { repo.deleteAndReturn(bookId) }
                .onSuccess { deleted ->
                    lastDeleted = deleted
                    _ui.update { it.copy(isDeleting = false) }
                    if (deleted != null) _events.emit(BookDetailEvent.Deleted(deleted.title))
                }
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

    fun undoDelete() {
        val restore = lastDeleted ?: return

        viewModelScope.launch {
            runCatching { repo.upsert(book = restore) }
                .onSuccess { lastDeleted = null }
                .onFailure { e -> _ui.update { it.copy(error = e.message ?: "Failed to undo") } }
        }
    }
}
