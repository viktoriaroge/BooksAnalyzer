package com.viroge.booksanalyzer.ui.screens.books.confirm

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.model.BookCoverDataSeed
import com.viroge.booksanalyzer.domain.model.BookSeed
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.TempBook
import com.viroge.booksanalyzer.domain.usecase.book.SaveBookUseCase
import com.viroge.booksanalyzer.domain.usecase.selection.ObserveBookCoverUrlSelectionUseCase
import com.viroge.booksanalyzer.domain.usecase.selection.ObserveTempBookSelectionUseCase
import com.viroge.booksanalyzer.domain.usecase.selection.SelectBookCoverDataSeedUseCase
import com.viroge.booksanalyzer.domain.usecase.selection.SelectBookSeedUseCase
import com.viroge.booksanalyzer.ui.nav.Routes
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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmBookViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeTempBookSelectionUseCase: ObserveTempBookSelectionUseCase,
    private val observeBookCoverUrlSelectionUseCase: ObserveBookCoverUrlSelectionUseCase,
    private val selectBookSeedUseCase: SelectBookSeedUseCase,
    private val selectBookCoverDataSeedUseCase: SelectBookCoverDataSeedUseCase,
    private val saveBookUseCase: SaveBookUseCase,
    private val mapper: ConfirmBookMapper,
) : ViewModel() {

    private val navSeed: TempBook? = savedStateHandle[Routes.TEMP_BOOK_SEED_ARG]

    private val _events = Channel<ConfirmBookEvent>(Channel.BUFFERED)
    val events: Flow<ConfirmBookEvent> = _events.receiveAsFlow()

    private val _manualInputState: MutableStateFlow<ConfirmBookScreenState.ManualInput> = MutableStateFlow(
        ConfirmBookScreenState.ManualInput(
            bookData = navSeed?.let { mapper.mapToDataState(it, null) },
            editTitle = navSeed?.title.orEmpty(),
            showTitleError = false,
            editAuthors = navSeed?.authors?.joinToString(separator = ", ").orEmpty(),
            showAuthorError = false,
            editYear = navSeed?.year.orEmpty(),
            editIsbn13 = navSeed?.isbn13.orEmpty(),
            isSaving = false,
            screenValues = mapper.getScreenManualValues(),
        )
    )

    val state = combine(
        _manualInputState,
        observeBookCoverUrlSelectionUseCase(),
        observeTempBookSelectionUseCase(), // temp book, not in DB (both confirm and manual)
    ) { internalState, selectedCoverUrl, selectedBook ->

        when (selectedBook?.source) {
            BookSource.GOOGLE_BOOKS, BookSource.OPEN_LIBRARY -> ConfirmBookUiState(
                screenState = ConfirmBookScreenState.DefaultData(
                    bookData = mapper.mapToDataState(selectedBook, selectedCoverUrl),
                    isSaving = false,
                    screenValues = mapper.getScreenDefaultValues(),
                ),
            )

            BookSource.MANUAL -> ConfirmBookUiState(
                screenState = internalState,
            )

            null -> null
        }

    }.filterNotNull()
        .distinctUntilChanged()
        .flowOn(Dispatchers.Default)
        .catch { _ -> Log.e("ConfirmBookViewModel", "Failed to prepare ui state.") } // TODO: Send error to UI
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = ConfirmBookUiState(
                screenState = ConfirmBookScreenState.Loading(
                    bookData = navSeed?.let { mapper.mapToDataState(navSeed, null) },
                    isManual = navSeed?.source == BookSource.MANUAL,
                    screenValues = mapper.getScreenLoadingValues(navSeed?.source == BookSource.MANUAL),
                )
            )
        )

    fun saveBook() {
        if (_manualInputState.value.isSaving) return

        viewModelScope.launch {
            _manualInputState.update { it.copy(isSaving = true) }

            val originalBook = observeTempBookSelectionUseCase().firstOrNull() ?: return@launch
            val selectedCoverUrl = observeBookCoverUrlSelectionUseCase().firstOrNull()
            val editedBook = originalBook.copy(coverUrl = selectedCoverUrl ?: originalBook.coverUrl)

            saveBookUseCase(editedBook)
                .onSuccess { result ->
                    val book = result.book
                    val seed = BookSeed(
                        id = book.id,
                        url = book.coverUrl ?: "",
                        animationKey = BookTransitionKey.calculate(
                            title = book.title,
                            authors = book.authors,
                            isbn = book.isbn13,
                            source = book.source,
                            sourceId = book.sourceId,
                        )
                    )
                    selectBookSeedUseCase(seed)

                    _events.send(ConfirmBookEvent.OpenBookDetails(seed))
                    _manualInputState.update { it.copy(isSaving = false) }
                }
                .onFailure { _ ->
                    _events.send(ConfirmBookEvent.Error(ConfirmErrorType.SAVING_FAILED))
                    _manualInputState.update { it.copy(isSaving = false) }
                }
        }
    }

    fun saveManualBook() {
        if (_manualInputState.value.isSaving) return

        viewModelScope.launch {
            val book = observeTempBookSelectionUseCase().firstOrNull() ?: return@launch
            val editState = _manualInputState.value

            val editTitle = editState.editTitle.trim()
            val editAuthor = editState.editAuthors.trim()

            if (editTitle.isBlank() || editAuthor.isBlank()) {
                // There are error states, check which ones to show:
                if (editTitle.isBlank()) _manualInputState.update { it.copy(showTitleError = true) }
                else _manualInputState.update { it.copy(showTitleError = false) }

                if (editAuthor.isBlank()) _manualInputState.update { it.copy(showAuthorError = true) }
                else _manualInputState.update { it.copy(showAuthorError = false) }

                return@launch
            }

            _manualInputState.update { it.copy(isSaving = true) }

            val editedBook = book.copy(
                title = editState.editTitle,
                authors = editState.editAuthors.split(",").map { it.trim() },
                year = editState.editYear,
                isbn13 = editState.editIsbn13,
                coverUrl = observeBookCoverUrlSelectionUseCase().firstOrNull(),
            )

            saveBookUseCase(editedBook)
                .onSuccess { result ->
                    val book = result.book
                    val seed = BookSeed(
                        id = book.id,
                        url = book.coverUrl ?: "",
                        animationKey = BookTransitionKey.calculate(
                            title = book.title,
                            authors = book.authors,
                            isbn = book.isbn13,
                            source = book.source,
                            sourceId = book.sourceId,
                        )
                    )
                    selectBookSeedUseCase(seed)

                    _events.send(ConfirmBookEvent.OpenBookDetails(seed))
                    _manualInputState.update { it.copy(isSaving = false) }
                }
                .onFailure { _ ->
                    _events.send(ConfirmBookEvent.Error(ConfirmErrorType.SAVING_FAILED))
                    _manualInputState.update { it.copy(isSaving = false) }
                }
        }
    }

    fun onTitleChange(value: String) = _manualInputState.update { it.copy(editTitle = value) }
    fun onAuthorsChange(value: String) = _manualInputState.update { it.copy(editAuthors = value) }
    fun onYearChange(value: String) = _manualInputState.update { it.copy(editYear = value) }
    fun onIsbnChange(value: String) = _manualInputState.update { it.copy(editIsbn13 = value) }

    fun onOpenCoverPicker(
        selectedCoverUrl: String?,
        originalCoverUrl: String?,
        isbn13: String?,
        source: BookSource,
        sourceId: String?
    ) {
        viewModelScope.launch {
            val seed = BookCoverDataSeed(
                selectedCoverUrl = selectedCoverUrl,
                originalCoverUrl = originalCoverUrl,
                isbn13 = isbn13,
                source = source,
                sourceId = sourceId,
            )
            selectBookCoverDataSeedUseCase(seed)

            _events.send(ConfirmBookEvent.OpenBookCoverPicker)
        }
    }

    fun onOpenCoverPickerInManualInputMode() {
        viewModelScope.launch {
            val editState = _manualInputState.value
            val seed = BookCoverDataSeed(
                selectedCoverUrl = observeBookCoverUrlSelectionUseCase().firstOrNull(),
                originalCoverUrl = null,
                isbn13 = editState.editIsbn13,
                source = BookSource.MANUAL,
                sourceId = null,
            )
            selectBookCoverDataSeedUseCase(seed)

            _events.send(ConfirmBookEvent.OpenBookCoverPicker)
        }
    }
}
