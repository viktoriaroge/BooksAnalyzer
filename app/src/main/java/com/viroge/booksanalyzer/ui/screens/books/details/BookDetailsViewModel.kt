package com.viroge.booksanalyzer.ui.screens.books.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import com.viroge.booksanalyzer.domain.usecase.EditBookUseCase
import com.viroge.booksanalyzer.domain.usecase.GetBookUseCase
import com.viroge.booksanalyzer.domain.usecase.MarkBookAsOpenedUseCase
import com.viroge.booksanalyzer.domain.usecase.UpdateBookStatusUseCase
import com.viroge.booksanalyzer.ui.nav.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    getBook: GetBookUseCase,
    private val editBook: EditBookUseCase,
    private val updateBookStatus: UpdateBookStatusUseCase,
    private val markBookAsOpened: MarkBookAsOpenedUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val bookId: String = checkNotNull(savedStateHandle[Routes.ARG_BOOK_ID])

    private val bookStream: Flow<Result<Book>> = getBook(bookId)
        .map { Result.success(it) }
        .catch { emit(Result.failure(it)) }

    private val _state = MutableStateFlow(BookDetailsUiState())
    val state: StateFlow<BookDetailsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            markBookAsOpened(bookId)

            bookStream.collect { result ->
                result.fold(
                    onSuccess = { book ->
                        _state.update { it.copy(book = book, error = null) }
                    },
                    onFailure = { e ->
                        _state.update { it.copy(error = e.message ?: "Failed to load book") }
                    })
            }
        }
    }

    fun setStatus(
        status: ReadingStatus,
    ) {
        viewModelScope.launch {
            updateBookStatus(bookId, status)
                .onSuccess {
                    val book = _state.value.book ?: return@onSuccess

                    _state.update { it.copy(book = book.copy(status = status)) }
                }
                .onFailure { e ->
                    _state.update { it.copy(error = e.message ?: "Failed to update status") }
                }
        }
    }

    fun enterEditMode() {
        val book = _state.value.book ?: return

        _state.update {
            it.copy(
                isEditMode = true,
                editTitle = book.title,
                editAuthors = book.authors.joinToString(separator = ", "),
                editPublishedYear = book.publishedYear.orEmpty(),
                editIsbn13 = book.isbn13.orEmpty(),
                editIsbn10 = book.isbn10.orEmpty(),
                error = null,
            )
        }
    }

    fun exitEditMode() {
        _state.update {
            it.copy(
                isEditMode = false,
                editTitle = "",
                editAuthors = "",
                editPublishedYear = "",
                editIsbn13 = "",
                editIsbn10 = "",
                error = null,
            )
        }
    }

    fun updateEditTitle(value: String) {
        _state.update { it.copy(editTitle = value) }
    }

    fun updateEditAuthors(value: String) {
        _state.update { it.copy(editAuthors = value) }
    }

    fun updateEditPublishedYear(value: String) {
        _state.update { it.copy(editPublishedYear = value) }
    }

    fun updateEditIsbn13(value: String) {
        _state.update { it.copy(editIsbn13 = value) }
    }

    fun updateEditIsbn10(value: String) {
        _state.update { it.copy(editIsbn10 = value) }
    }

    fun saveEdits(
        selectedCoverUrl: String?,
        selectedCoverHeaders: Map<String, String>,
    ) {
        val state = _state.value
        val book = state.book ?: return

        val title = state.editTitle.trim()
        if (title.isBlank()) {
            _state.update { it.copy(error = "Title is required") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val updated = book.copy(
                title = title,
                authors = state.editAuthors.split(",").map { it.trim() }.filter { it.isNotBlank() }.ifEmpty { listOf("") },
                publishedYear = state.editPublishedYear.trim(),
                isbn13 = state.editIsbn13.trim().takeIf { it.isNotEmpty() },
                isbn10 = state.editIsbn10.trim().takeIf { it.isNotEmpty() },
                coverUrl = selectedCoverUrl ?: book.coverUrl,
                coverRequestHeaders = selectedCoverHeaders,
            )

            editBook(updated)
                .onSuccess { _ ->
                    _state.update {
                        it.copy(
                            book = updated,
                            isEditMode = false,
                            isSaving = false,
                            editTitle = "",
                            editAuthors = "",
                            editPublishedYear = "",
                            editIsbn13 = "",
                            editIsbn10 = "",
                            error = null,
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            error = e.message ?: "Failed to save changes",
                        )
                    }
                }
        }
    }
}
