package com.viroge.booksanalyzer.ui.books.details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.domain.Book
import com.viroge.booksanalyzer.domain.ReadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val repo: BooksRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val bookId: String = checkNotNull(savedStateHandle["bookId"])

    private val _ui = MutableStateFlow(value = BookDetailsUiState())
    val ui: StateFlow<BookDetailsUiState> = _ui.asStateFlow()

    init {
        repo.observeBook(bookId)
            .onEach { book ->
                Log.d("BookDetailsViewModel", "state: state on book observed changed")
                _ui.update { it.copy(book = book, error = null) }
            }
            .catch { e ->
                Log.d("BookDetailsViewModel", "state: an error while observing book")
                _ui.update { it.copy(error = e.message ?: "Failed to load book") }
            }
            .launchIn(viewModelScope)
    }

    fun setStatus(
        status: ReadingStatus,
    ) {
        viewModelScope.launch {
            runCatching { repo.updateStatus(bookId, status) }
                .onFailure { e ->
                    Log.d("BookDetailsViewModel", "state: change status fail")
                    _ui.update { it.copy(error = e.message ?: "Failed to update status") }
                }
        }
    }

    fun updateLastOpenDelayed() {
        viewModelScope.launch {
            delay(timeMillis = 450)
            repo.updateOnOpen(bookId)
        }
    }

    fun enterEditMode() {
        val book = _ui.value.book ?: return
        Log.d("BookDetailsViewModel", "state: enter edit mode")
        _ui.update {
            it.copy(
                isEditMode = true,
                editTitle = book.title,
                editAuthors = book.authors.joinToString(separator = ", "),
                editPublishedYear = book.publishedYear?.toString().orEmpty(),
                editIsbn13 = book.isbn13.orEmpty(),
                editIsbn10 = book.isbn10.orEmpty(),
                editStatus = book.status,
                error = null,
            )
        }
    }

    fun exitEditMode() {
        Log.d("BookDetailsViewModel", "state: exit edit mode")
        _ui.update {
            it.copy(
                isEditMode = false,
                editTitle = "",
                editAuthors = "",
                editPublishedYear = "",
                editIsbn13 = "",
                editIsbn10 = "",
                editStatus = null,
                error = null,
            )
        }
    }

    fun updateEditTitle(value: String) {
        _ui.update { it.copy(editTitle = value) }
    }

    fun updateEditAuthors(value: String) {
        _ui.update { it.copy(editAuthors = value) }
    }

    fun updateEditPublishedYear(value: String) {
        _ui.update { it.copy(editPublishedYear = value) }
    }

    fun updateEditIsbn13(value: String) {
        _ui.update { it.copy(editIsbn13 = value) }
    }

    fun updateEditIsbn10(value: String) {
        _ui.update { it.copy(editIsbn10 = value) }
    }

    fun updateEditStatus(status: ReadingStatus) {
        _ui.update { it.copy(editStatus = status) }
    }

    fun saveEdits(selectedCoverUrl: String?) {
        val state = _ui.value
        val book = state.book ?: return

        val title = state.editTitle.trim()
        if (title.isBlank()) {
            _ui.update { it.copy(error = "Title is required") }
            return
        }

        viewModelScope.launch {
            Log.d("BookDetailsViewModel", "state: save edit started")
            _ui.update { it.copy(isSaving = true, error = null) }
            val updated = book.copy(
                title = title,
                authors = state.editAuthors.split(",").map { it.trim() }.filter { it.isNotBlank() }.ifEmpty { listOf("") },
                publishedYear = state.editPublishedYear.trim().toIntOrNull(),
                isbn13 = state.editIsbn13.trim().takeIf { it.isNotEmpty() },
                isbn10 = state.editIsbn10.trim().takeIf { it.isNotEmpty() },
                coverUrl = selectedCoverUrl ?: book.coverUrl,
                status = state.editStatus ?: ReadingStatus.NOT_STARTED,
            )

            runCatching { repo.insertFromBook(book = updated, wasEdited = true) }
                .onSuccess {
                    Log.d("BookDetailsViewModel", "state: save edit success")
                    _ui.update {
                        it.copy(
                            book = updated,
                            isEditMode = false,
                            isSaving = false,
                            editTitle = "",
                            editAuthors = "",
                            editPublishedYear = "",
                            editIsbn13 = "",
                            editIsbn10 = "",
                            editStatus = null,
                            error = null,
                        )
                    }
                }
                .onFailure { e ->
                    Log.d("BookDetailsViewModel", "state: save edit fail")
                    _ui.update {
                        it.copy(
                            isSaving = false,
                            error = e.message ?: "Failed to save changes",
                        )
                    }
                }
        }
    }
}

data class BookDetailsUiState(
    val book: Book? = null,
    val isDeleting: Boolean = false,
    val error: String? = null,
    val isEditMode: Boolean = false,
    val isSaving: Boolean = false,
    val editTitle: String = "",
    val editAuthors: String = "",
    val editPublishedYear: String = "",
    val editIsbn13: String = "",
    val editIsbn10: String = "",
    val editStatus: ReadingStatus? = null,
)
