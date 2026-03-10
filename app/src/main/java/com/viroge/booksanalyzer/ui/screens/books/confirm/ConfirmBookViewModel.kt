package com.viroge.booksanalyzer.ui.screens.books.confirm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.provider.BookSearchStateProvider
import com.viroge.booksanalyzer.domain.provider.BookSelectionStateProvider
import com.viroge.booksanalyzer.domain.provider.CoverPickerStateProvider
import com.viroge.booksanalyzer.domain.usecase.SaveBookUseCase
import com.viroge.booksanalyzer.domain.usecase.ValidateAndGetManualBookUseCase
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
    bookSearchStateProvider: BookSearchStateProvider,
    private val coverPickerStateProvider: CoverPickerStateProvider,
    private val saveBookUseCase: SaveBookUseCase,
    private val validateManualBook: ValidateAndGetManualBookUseCase,
    private val mapper: ConfirmBookMapper,
) : ViewModel() {

    private val _events = MutableSharedFlow<ConfirmEvent>()
    val events = _events.asSharedFlow()

    private val _internalState = MutableStateFlow(ConfirmBookScreenState())
    val state = combine(
        _internalState,
        coverPickerStateProvider.state,
        bookSelectionStateProvider.selectedTempBook, // temp book, not in DB
        bookSearchStateProvider.prefillMode,
        bookSearchStateProvider.prefillQuery,
    ) { internalState, pickerState, selectedBook, prefillMode, prefillQuery ->

        val manualData = mapper.mapToManualFormData(prefillQuery, prefillMode)
        val newState = internalState.copy(
            screenValues = mapper.getScreenValues(),

            manualFormData = manualData,
            titleInput = internalState.titleInput.ifBlank { manualData?.title ?: "" },
            authorsInput = internalState.authorsInput.ifBlank { manualData?.authors ?: "" },
            isbn13Input = internalState.isbn13Input.ifBlank { manualData?.isbn13 ?: "" },
        )
        ConfirmBookUiState(
            screenState = newState,
            bookData = selectedBook?.let { mapper.mapToDataState(it, pickerState.selectedCandidate) },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = ConfirmBookUiState()
    )

    fun saveBook(book: Book? = null) {
        if (_internalState.value.isSaving) return

        viewModelScope.launch {
            _internalState.update { it.copy(isSaving = true) }

            val originalBook = book
                ?: bookSelectionStateProvider.getSelectedTempBook()
                ?: return@launch

            val selectedCover = coverPickerStateProvider.getSelected()

            val finalBook = originalBook.copy(
                coverUrl = selectedCover?.url ?: originalBook.coverUrl,
                coverRequestHeaders = selectedCover?.headers ?: originalBook.coverRequestHeaders,
            )

            saveBookUseCase(finalBook)
                .onSuccess { result ->
                    bookSelectionStateProvider.selectBookId(result.bookId)
                    _events.emit(ConfirmEvent.Saved)
                }
                .onFailure { _ ->
                    _events.emit(ConfirmEvent.Error(ConfirmErrorType.SAVING_FAILED))
                }

            _internalState.update { it.copy(isSaving = false) }
        }
    }

    fun saveManualBook() {
        viewModelScope.launch {
            val state = _internalState.value
            validateManualBook(
                title = state.titleInput,
                authors = state.authorsInput,
                year = state.yearInput,
                isbn13 = state.isbn13Input,
            ).onSuccess { book ->
                saveBook(book)
            }.onFailure { _ ->
                _events.emit(ConfirmEvent.Error(ConfirmErrorType.SAVING_FAILED))
            }
        }
    }

    fun onTitleChange(value: String) = _internalState.update { it.copy(titleInput = value) }
    fun onAuthorsChange(value: String) = _internalState.update { it.copy(authorsInput = value) }
    fun onYearChange(value: String) = _internalState.update { it.copy(yearInput = value) }
    fun onIsbnChange(value: String) = _internalState.update { it.copy(isbn13Input = value) }

    fun getTemporaryBookForCoverPicker(
        source: BookSource,
        coverUrl: String?,
    ): Book {
        val state = _internalState.value
        return mapper.mapToTempBookForCoverPicker(
            title = state.titleInput,
            authors = state.authorsInput,
            publishedYear = state.yearInput,
            isbn13 = state.isbn13Input,
            source = source,
            coverUrl = coverUrl,
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
