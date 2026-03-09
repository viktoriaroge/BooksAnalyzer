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
    private val mapper: BookDetailsMapper,
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
            _state.update {
                it.copy(
                    isLoading = true,
                    screenValues = mapper.getScreenValues(isInEditMode = false),
                    deleteDialogValues = mapper.getDeleteDialogValues(),
                )
            }
            markBookAsOpened(bookId)

            bookStream.collect { result ->
                result.fold(
                    onSuccess = { book ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                book = book,
                                errorState = mapper.getErrorState(BookDetailsErrorType.NONE),
                            )
                        }
                    },
                    onFailure = { _ ->
                        _state.update {
                            it.copy(
                                isLoading = true,
                                errorState = mapper.getErrorState(BookDetailsErrorType.LOADING_BOOK_FAILED),
                            )
                        }
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
                .onFailure { _ ->
                    _state.update {
                        it.copy(errorState = mapper.getErrorState(BookDetailsErrorType.UPDATING_STATUS_FAILED))
                    }
                }
        }
    }

    fun enterEditMode() {
        val book = _state.value.book ?: return

        val editState = BookDetailsEditState(
            editTitle = book.title,
            editAuthors = book.authors.joinToString(separator = ", "),
            editPublishedYear = book.publishedYear.orEmpty(),
            editIsbn13 = book.isbn13.orEmpty(),
            editIsbn10 = book.isbn10.orEmpty(),
        )
        val isInEditMode = true
        _state.update {
            it.copy(
                isEditMode = isInEditMode,
                editState = editState,
                screenValues = mapper.getScreenValues(isInEditMode = isInEditMode),
                errorState = mapper.getErrorState(BookDetailsErrorType.NONE),
            )
        }
    }

    fun exitEditMode() {
        val editState = BookDetailsEditState(
            editTitle = "",
            editAuthors = "",
            editPublishedYear = "",
            editIsbn13 = "",
            editIsbn10 = "",
        )
        val isInEditMode = false
        _state.update {
            it.copy(
                isEditMode = isInEditMode,
                editState = editState,
                screenValues = mapper.getScreenValues(isInEditMode = isInEditMode),
                errorState = mapper.getErrorState(BookDetailsErrorType.NONE),
            )
        }
    }

    fun updateEditTitle(value: String) {
        _state.update { it.copy(editState = it.editState.copy(editTitle = value)) }
    }

    fun updateEditAuthors(value: String) {
        _state.update { it.copy(editState = it.editState.copy(editAuthors = value)) }
    }

    fun updateEditPublishedYear(value: String) {
        _state.update { it.copy(editState = it.editState.copy(editPublishedYear = value)) }
    }

    fun updateEditIsbn13(value: String) {
        _state.update { it.copy(editState = it.editState.copy(editIsbn13 = value)) }
    }

    fun updateEditIsbn10(value: String) {
        _state.update { it.copy(editState = it.editState.copy(editIsbn10 = value)) }
    }

    fun saveEdits(
        selectedCoverUrl: String?,
        selectedCoverHeaders: Map<String, String>,
    ) {
        val state = _state.value
        val book = state.book ?: return

        val editState = state.editState
        val editTitle = editState.editTitle.trim()
        if (editTitle.isBlank()) {
            _state.update {
                it.copy(
                    errorState = mapper.getErrorState(BookDetailsErrorType.TITLE_REQUIRED),
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSaving = true,
                    errorState = mapper.getErrorState(BookDetailsErrorType.NONE),
                )
            }

            val updatedBook = book.copy(
                title = editTitle,
                authors = editState.editAuthors.split(",").map { it.trim() }.filter { it.isNotBlank() }.ifEmpty { listOf("") },
                publishedYear = editState.editPublishedYear.trim(),
                isbn13 = editState.editIsbn13.trim().takeIf { it.isNotEmpty() },
                isbn10 = editState.editIsbn10.trim().takeIf { it.isNotEmpty() },
                coverUrl = selectedCoverUrl ?: book.coverUrl,
                coverRequestHeaders = selectedCoverHeaders,
            )
            val clearEditState = BookDetailsEditState(
                editTitle = "",
                editAuthors = "",
                editPublishedYear = "",
                editIsbn13 = "",
                editIsbn10 = "",
            )
            editBook(updatedBook)
                .onSuccess { _ ->
                    _state.update {
                        it.copy(
                            book = updatedBook,
                            editState = clearEditState,
                            isEditMode = false,
                            isSaving = false,
                            errorState = mapper.getErrorState(BookDetailsErrorType.NONE),
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorState = mapper.getErrorState(BookDetailsErrorType.SAVING_FAILED),
                        )
                    }
                }
        }
    }
}

enum class BookDetailsErrorType {
    NONE,
    LOADING_BOOK_FAILED,
    UPDATING_STATUS_FAILED,
    SAVING_FAILED,
    TITLE_REQUIRED,
}
