package com.viroge.booksanalyzer.ui.books.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.data.local.books.BookEntity
import com.viroge.booksanalyzer.domain.BooksUtil
import com.viroge.booksanalyzer.domain.ReadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    fun updateLastOpenDelayed() {
        viewModelScope.launch {
            delay(timeMillis = 450)
            repo.updateOnOpen(bookId)
        }
    }

    fun enterEditMode() {
        val b = _ui.value.book ?: return
        _ui.update {
            it.copy(
                isEditMode = true,
                editTitle = b.title,
                editAuthors = b.authors,
                editPublishedYear = b.publishedYear?.toString().orEmpty(),
                editIsbn13 = b.isbn13.orEmpty(),
                editIsbn10 = b.isbn10.orEmpty(),
                editCoverUrl = b.coverUrl.orEmpty(),
                editStatus = runCatching { ReadingStatus.valueOf(b.status) }
                    .getOrDefault(ReadingStatus.NOT_STARTED),
                error = null,
            )
        }
    }

    fun exitEditMode() {
        _ui.update {
            it.copy(
                isEditMode = false,
                editTitle = "",
                editAuthors = "",
                editPublishedYear = "",
                editIsbn13 = "",
                editIsbn10 = "",
                editCoverUrl = "",
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

    fun updateEditCoverUrl(value: String) {
        _ui.update { it.copy(editCoverUrl = value) }
    }

    fun updateEditStatus(status: ReadingStatus) {
        _ui.update { it.copy(editStatus = status) }
    }

    fun saveEdits() {
        val state = _ui.value
        val book = state.book ?: return

        val title = state.editTitle.trim()
        if (title.isBlank()) {
            _ui.update { it.copy(error = "Title is required") }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(isSaving = true, error = null) }

            val authorsList = state.editAuthors
                .split(',')
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            val year = state.editPublishedYear.trim().toIntOrNull()
            val titleKey = BooksUtil.titleKey(title, authorsList, year)

            val updated = book.copy(
                title = title,
                authors = state.editAuthors.trim(),
                titleKey = titleKey,
                publishedYear = year,
                isbn13 = state.editIsbn13.trim().takeIf { it.isNotEmpty() },
                isbn10 = state.editIsbn10.trim().takeIf { it.isNotEmpty() },
                coverUrl = state.editCoverUrl.trim().takeIf { it.isNotEmpty() },
                status = (state.editStatus ?: ReadingStatus.NOT_STARTED).name,
            )

            runCatching { repo.upsert(updated) }
                .onSuccess {
                    _ui.update {
                        it.copy(
                            isEditMode = false,
                            isSaving = false,
                            editTitle = "",
                            editAuthors = "",
                            editPublishedYear = "",
                            editIsbn13 = "",
                            editIsbn10 = "",
                            editCoverUrl = "",
                            editStatus = null,
                            error = null,
                        )
                    }
                }
                .onFailure { e ->
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
    val book: BookEntity? = null,
    val isDeleting: Boolean = false,
    val error: String? = null,
    val isEditMode: Boolean = false,
    val isSaving: Boolean = false,
    val editTitle: String = "",
    val editAuthors: String = "",
    val editPublishedYear: String = "",
    val editIsbn13: String = "",
    val editIsbn10: String = "",
    val editCoverUrl: String = "",
    val editStatus: ReadingStatus? = null,
)

sealed interface BookDetailEvent {

    data class Deleted(
        val title: String,
    ) : BookDetailEvent
}
