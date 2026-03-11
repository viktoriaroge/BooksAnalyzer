package com.viroge.booksanalyzer.ui.screens.books.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import com.viroge.booksanalyzer.domain.provider.BookSelectionStateProvider
import com.viroge.booksanalyzer.domain.provider.CoverPickerStateProvider
import com.viroge.booksanalyzer.domain.usecase.EditBookUseCase
import com.viroge.booksanalyzer.domain.usecase.MarkBookAsOpenedUseCase
import com.viroge.booksanalyzer.domain.usecase.ObserveBookUseCase
import com.viroge.booksanalyzer.domain.usecase.UpdateBookStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val bookSelectionStateProvider: BookSelectionStateProvider,
    private val coverPickerStateProvider: CoverPickerStateProvider,
    private val getBookUseCase: ObserveBookUseCase,
    private val editBookUseCase: EditBookUseCase,
    private val updateBookStatus: UpdateBookStatusUseCase,
    private val markBookAsOpened: MarkBookAsOpenedUseCase,
    private val mapper: BookDetailsMapper,
) : ViewModel() {

    private var needsMarking: Boolean = true

    private val _events = MutableSharedFlow<DetailsEvent>()
    val events = _events.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val bookStream: Flow<Book?> = bookSelectionStateProvider.selectedBookId
        .flatMapLatest { id -> id?.let { getBookUseCase(it) } ?: flowOf(null) }
        .distinctUntilChanged()
        .catch { _ -> _events.emit(DetailsEvent.Error(DetailsErrorType.LOADING_BOOK_FAILED)) }

    private val _internalState = MutableStateFlow(BookDetailsScreenState())
    private val _selectedBookState: MutableStateFlow<BookDetailsDataState?> = MutableStateFlow(null)

    val state: StateFlow<BookDetailsUiState> = combine(
        _internalState,
        bookStream,
        coverPickerStateProvider.state,
    ) { internalState, selectedBook, pickerState ->

        if (selectedBook != null && needsMarking) {
            markBookAsOpened(selectedBook.id)
            needsMarking = false
        }

        val newState = internalState.copy(
            screenValues = mapper.getScreenValues(),
            editScreenValues = mapper.getEditScreenValues(),
            deleteDialogValues = mapper.getDeleteDialogValues(),
        )

        val bookData = selectedBook?.let { mapper.mapToDataState(it, pickerState.selectedCandidate) }
        _selectedBookState.value = bookData

        BookDetailsUiState(
            screenState = newState,
            bookData = bookData,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = BookDetailsUiState()
    )

    init {
        needsMarking = true
    }

    fun setStatus(
        status: ReadingStatus,
    ) {
        viewModelScope.launch {
            val bookId = bookSelectionStateProvider.getSelectedBookId() ?: return@launch

            updateBookStatus(bookId, status)
                .onFailure { _ -> _events.emit(DetailsEvent.Error(DetailsErrorType.UPDATING_STATUS_FAILED)) }
        }
    }

    fun enterEditMode() {
        val book = _selectedBookState.value ?: return

        _internalState.update {
            it.copy(
                isInEditMode = true,
                editState = BookDetailsEditState(
                    editTitle = book.title,
                    showTitleError = false,
                    editAuthors = book.authors,
                    showAuthorError = false,
                    editYear = book.year.orEmpty(),
                    editIsbn13 = book.isbn13.orEmpty(),
                    editIsbn10 = book.isbn10.orEmpty(),
                ),
            )
        }
    }

    fun exitEditMode() {
        _internalState.update {
            it.copy(
                isInEditMode = false,
                editState = BookDetailsEditState(
                    editTitle = "",
                    showTitleError = false,
                    editAuthors = "",
                    showAuthorError = false,
                    editYear = "",
                    editIsbn13 = "",
                    editIsbn10 = "",
                ),
            )
        }
    }

    fun updateEditTitle(value: String) {
        _internalState.update { it.copy(editState = it.editState.copy(editTitle = value)) }
    }

    fun updateEditAuthors(value: String) {
        _internalState.update { it.copy(editState = it.editState.copy(editAuthors = value)) }
    }

    fun updateEditPublishedYear(value: String) {
        _internalState.update { it.copy(editState = it.editState.copy(editYear = value)) }
    }

    fun updateEditIsbn13(value: String) {
        _internalState.update { it.copy(editState = it.editState.copy(editIsbn13 = value)) }
    }

    fun updateEditIsbn10(value: String) {
        _internalState.update { it.copy(editState = it.editState.copy(editIsbn10 = value)) }
    }

    fun saveEdits() {
        val book = _selectedBookState.value ?: return

        val editState = _internalState.value.editState
        val editTitle = editState.editTitle.trim()
        val editAuthor = editState.editAuthors.trim()

        if (editTitle.isBlank() || editAuthor.isBlank()) {
            // There are error states, check which ones to show:
            if (editTitle.isBlank()) _internalState.update { it.copy(editState = it.editState.copy(showTitleError = true)) }
            else _internalState.update { it.copy(editState = it.editState.copy(showTitleError = false)) }

            if (editAuthor.isBlank()) _internalState.update { it.copy(editState = it.editState.copy(showAuthorError = true)) }
            else _internalState.update { it.copy(editState = it.editState.copy(showAuthorError = false)) }

            return
        }

        viewModelScope.launch {
            _internalState.update { it.copy(isSaving = true) }

            editBookUseCase(
                bookId = book.id,
                title = editTitle,
                authors = editState.editAuthors,
                year = editState.editYear.trim().takeIf { it.isNotEmpty() },
                isbn13 = editState.editIsbn13.trim().takeIf { it.isNotEmpty() },
                isbn10 = editState.editIsbn10.trim().takeIf { it.isNotEmpty() },
                coverUrl = coverPickerStateProvider.getSelected()?.url ?: book.url,
            ).onSuccess { _ ->
                _internalState.update {
                    it.copy(
                        editState = BookDetailsEditState(
                            editTitle = "",
                            editAuthors = "",
                            editYear = "",
                            editIsbn13 = "",
                            editIsbn10 = "",
                        ),
                        isInEditMode = false,
                        isSaving = false,
                    )
                }
            }.onFailure { _ ->
                _events.emit(DetailsEvent.Error(DetailsErrorType.SAVING_FAILED))
                _internalState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun clearSessionData() {
        viewModelScope.launch {
            delay(500) // Delay the cleanup until the screen is actually off-screen
            coverPickerStateProvider.clear()
            bookSelectionStateProvider.clearSelection()
            Log.d("BookDetailsViewModel", "---> Session data cleared")
        }
    }
}
