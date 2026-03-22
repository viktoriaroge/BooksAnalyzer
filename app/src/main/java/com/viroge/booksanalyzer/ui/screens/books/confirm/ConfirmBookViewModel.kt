package com.viroge.booksanalyzer.ui.screens.books.confirm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.TempBook
import com.viroge.booksanalyzer.domain.provider.BookSelectionStateProvider
import com.viroge.booksanalyzer.domain.provider.CoverPickerStateProvider
import com.viroge.booksanalyzer.domain.usecase.book.SaveBookUseCase
import com.viroge.booksanalyzer.ui.screens.books.BookTransitionKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
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

    private val _events = Channel<ConfirmEvent>(Channel.BUFFERED)
    val events: Flow<ConfirmEvent> = _events.receiveAsFlow()

    private val _internalState = MutableStateFlow(ConfirmBookScreenState())
    val state = combine(
        _internalState,
        coverPickerStateProvider.selectedCoverUrl,
        bookSelectionStateProvider.selectedTempBook, // temp book, not in DB (both confirm and manual)
    ) { internalState, selectedCoverUrl, selectedBook ->

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
            bookData = selectedBook?.let { mapper.mapToDataState(it, selectedCoverUrl) },
        )
    }.distinctUntilChanged()
        .flowOn(Dispatchers.Default)
        .catch { _ -> Log.e("ConfirmBookViewModel", "Failed to prepare ui state.") } // TODO: Send error to UI
        .stateIn(
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
            val editedBook = originalBook.copy(coverUrl = coverPickerStateProvider.getSelectedCoverUrl() ?: originalBook.coverUrl)

            saveBookUseCase(editedBook)
                .onSuccess { result ->
                    val book = result.book
                    bookSelectionStateProvider.selectBookSeed(
                        bookId = book.id,
                        bookCoverUrl = book.coverUrl ?: "",
                        bookAnimationKey = BookTransitionKey.calculate(
                            title = book.title,
                            authors = book.authors,
                            isbn = book.isbn13,
                            source = book.source,
                            sourceId = book.sourceId,
                        )
                    )
                    _events.send(ConfirmEvent.Saved)
                    _internalState.update { it.copy(isSaving = false) }
                }
                .onFailure { _ ->
                    _events.send(ConfirmEvent.Error(ConfirmErrorType.SAVING_FAILED))
                    _internalState.update { it.copy(isSaving = false) }
                }
        }
    }

    fun saveManualBook() {
        if (_internalState.value.isSaving) return

        viewModelScope.launch {
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

            _internalState.update { it.copy(isSaving = true) }

            val editedBook = book.copy(
                title = editState.editTitle,
                authors = editState.editAuthors.split(",").map { it.trim() },
                year = editState.editYear,
                isbn13 = editState.editIsbn13,
                coverUrl = coverPickerStateProvider.getSelectedCoverUrl(),
            )

            saveBookUseCase(editedBook)
                .onSuccess { result ->
                    val book = result.book
                    bookSelectionStateProvider.selectBookSeed(
                        bookId = book.id,
                        bookCoverUrl = book.coverUrl ?: "",
                        bookAnimationKey = BookTransitionKey.calculate(
                            title = book.title,
                            authors = book.authors,
                            isbn = book.isbn13,
                            source = book.source,
                            sourceId = book.sourceId,
                        )
                    )
                    _events.send(ConfirmEvent.Saved)
                    _internalState.update { it.copy(isSaving = false) }
                }
                .onFailure { _ ->
                    _events.send(ConfirmEvent.Error(ConfirmErrorType.SAVING_FAILED))
                    _internalState.update { it.copy(isSaving = false) }
                }
        }
    }

    fun onTitleChange(value: String) = _internalState.update { it.copy(editState = it.editState.copy(editTitle = value)) }
    fun onAuthorsChange(value: String) = _internalState.update { it.copy(editState = it.editState.copy(editAuthors = value)) }
    fun onYearChange(value: String) = _internalState.update { it.copy(editState = it.editState.copy(editYear = value)) }
    fun onIsbnChange(value: String) = _internalState.update { it.copy(editState = it.editState.copy(editIsbn13 = value)) }

    fun getTempManualBookForCoverPicker(): TempBook? {
        val book = bookSelectionStateProvider.getSelectedTempBook() ?: return null

        val editState = _internalState.value.editState
        return book.copy(
            title = editState.editTitle,
            authors = editState.editAuthors.split(",").map { it.trim() },
            year = editState.editYear,
            isbn13 = editState.editIsbn13,
            coverUrl = coverPickerStateProvider.getSelectedCoverUrl(),
            originalCoverUrl = coverPickerStateProvider.getSelectedCoverUrl(),
        )
    }

    override fun onCleared() {
        super.onCleared()
        coverPickerStateProvider.clear()
        bookSelectionStateProvider.clearTempSelection()
    }
}
