package com.viroge.booksanalyzer.ui.screens.books.confirm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.provider.BookSelectionStateProvider
import com.viroge.booksanalyzer.domain.provider.CoverPickerStateProvider
import com.viroge.booksanalyzer.domain.usecase.SaveBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmBookViewModel @Inject constructor(
    private val bookSelectionStateProvider: BookSelectionStateProvider,
    private val coverPickerStateProvider: CoverPickerStateProvider,
    private val saveBookUseCase: SaveBookUseCase,
    private val mapper: ConfirmBookMapper,
) : ViewModel() {

    private var needsInitializing: Boolean = true

    private val _events = MutableSharedFlow<ConfirmEvent>()
    val events = _events.asSharedFlow()

    private val _internalState = MutableStateFlow(ConfirmBookScreenState())
    val state = combine(
        _internalState,
        coverPickerStateProvider.state,
        bookSelectionStateProvider.selectedTempBook, // temp book, not in DB (both confirm and manual)
    ) { internalState, pickerState, selectedBook ->

        val newState = internalState.copy(
            screenValues = mapper.getScreenValues(),
            isInManualMode = selectedBook?.source == BookSource.MANUAL,
        )
        if (selectedBook != null && needsInitializing) {
            val stateWithInitialEditFields = newState.copy(
                editState = newState.editState.copy(
                    editTitle = selectedBook.title,
                    editAuthors = selectedBook.authors.joinToString(separator = ", "),
                    editYear = selectedBook.year.orEmpty(),
                    editIsbn13 = selectedBook.isbn13.orEmpty(),
                ),
            )
            needsInitializing = false
            _internalState.update { stateWithInitialEditFields } // just once at initializing, will retrigger the source above
        }

        ConfirmBookUiState(
            screenState = newState,
            bookData = selectedBook?.let { mapper.mapToDataState(it, pickerState.selectedCandidate) },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = ConfirmBookUiState()
    )

    init {
        needsInitializing = true
    }

    fun saveBook() {
        if (_internalState.value.isSaving) return

        viewModelScope.launch {
            _internalState.update { it.copy(isSaving = true) }

            val originalBook = bookSelectionStateProvider.getSelectedTempBook() ?: return@launch
            val editedBook = originalBook.copy(coverUrl = coverPickerStateProvider.getSelected()?.url ?: originalBook.coverUrl)

            saveBookUseCase(editedBook)
                .onSuccess { result ->
                    bookSelectionStateProvider.selectBookId(result.bookId)
                    _events.emit(ConfirmEvent.Saved)
                    _internalState.update { it.copy(isSaving = false) }
                }
                .onFailure { _ ->
                    _events.emit(ConfirmEvent.Error(ConfirmErrorType.SAVING_FAILED))
                    _internalState.update { it.copy(isSaving = false) }
                }
        }
    }

    fun saveManualBook() {
        if (_internalState.value.isSaving) return

        viewModelScope.launch {
            _internalState.update { it.copy(isSaving = true) }

            val book = bookSelectionStateProvider.getSelectedTempBook() ?: return@launch
            val editState = _internalState.value.editState

            val editTitle = editState.editTitle.trim()
            val editAuthor = editState.editAuthors.trim()

            if (editTitle.isBlank() || editAuthor.isBlank()) {
                // There are error states, check which ones to show:
                if (editTitle.isBlank()) _internalState.update { it.copy(editState = it.editState.copy(showTitleError = true)) }
                else _internalState.update { it.copy(editState = it.editState.copy(showTitleError = false)) }

                if (editAuthor.isBlank()) _internalState.update { it.copy(editState = it.editState.copy(showAuthorError = true)) }
                else _internalState.update { it.copy(editState = it.editState.copy(showAuthorError = false)) }

                return@launch
            }
            val editedBook = book.copy(
                title = editState.editTitle,
                authors = editState.editAuthors.split(",").map { it.trim() },
                year = editState.editYear,
                isbn13 = editState.editIsbn13,
                coverUrl = coverPickerStateProvider.getSelected()?.url,
            )

            saveBookUseCase(editedBook)
                .onSuccess { result ->
                    bookSelectionStateProvider.selectBookId(result.bookId)
                    _events.emit(ConfirmEvent.Saved)
                    _internalState.update { it.copy(isSaving = false) }
                }
                .onFailure { _ ->
                    _events.emit(ConfirmEvent.Error(ConfirmErrorType.SAVING_FAILED))
                    _internalState.update { it.copy(isSaving = false) }
                }
        }
    }

    fun onTitleChange(value: String) = _internalState.update { it.copy(editState = it.editState.copy(editTitle = value)) }
    fun onAuthorsChange(value: String) = _internalState.update { it.copy(editState = it.editState.copy(editAuthors = value)) }
    fun onYearChange(value: String) = _internalState.update { it.copy(editState = it.editState.copy(editYear = value)) }
    fun onIsbnChange(value: String) = _internalState.update { it.copy(editState = it.editState.copy(editIsbn13 = value)) }

    fun getTemporaryBookForCoverPicker(): Book {
        val editState = _internalState.value.editState
        return mapper.mapToTempBookForCoverPicker(
            title = editState.editTitle,
            authors = editState.editAuthors,
            publishedYear = editState.editYear,
            isbn13 = editState.editIsbn13,
            source = BookSource.MANUAL,
            coverUrl = coverPickerStateProvider.getSelected()?.url,
        )
    }

    fun clearSessionData() {
        viewModelScope.launch {
            delay(500) // Delay the cleanup until the screen is actually off-screen
            coverPickerStateProvider.clear()
            bookSelectionStateProvider.clearTempSelection()
            Log.d("ConfirmBookViewModel", "---> Session data cleared")
        }
    }
}
