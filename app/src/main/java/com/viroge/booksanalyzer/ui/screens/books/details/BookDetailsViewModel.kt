package com.viroge.booksanalyzer.ui.screens.books.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.model.BookCoverDataSeed
import com.viroge.booksanalyzer.domain.model.BookSeed
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.usecase.book.EditBookUseCase
import com.viroge.booksanalyzer.domain.usecase.book.MarkBookAsOpenedUseCase
import com.viroge.booksanalyzer.domain.usecase.book.ObserveBookUseCase
import com.viroge.booksanalyzer.domain.usecase.book.UpdateBookStatusUseCase
import com.viroge.booksanalyzer.domain.usecase.selection.ObserveBookCoverUrlSelectionUseCase
import com.viroge.booksanalyzer.domain.usecase.selection.ObserveBookSeedSelectionUseCase
import com.viroge.booksanalyzer.domain.usecase.selection.SelectBookCoverDataSeedUseCase
import com.viroge.booksanalyzer.ui.nav.StateArguments
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val selectBookCoverDataSeedUseCase: SelectBookCoverDataSeedUseCase,
    private val observeBookCoverUrlSelectionUseCase: ObserveBookCoverUrlSelectionUseCase,
    private val observeBookSeedSelectionUseCase: ObserveBookSeedSelectionUseCase,
    private val getBookUseCase: ObserveBookUseCase,
    private val markBookAsOpened: MarkBookAsOpenedUseCase,
    private val updateBookStatusUseCase: UpdateBookStatusUseCase,
    private val editBookUseCase: EditBookUseCase,
    private val mapper: BookDetailsMapper,
) : ViewModel() {

    private val navSeed: BookSeed? = savedStateHandle[StateArguments.BOOK_SEED_ARG]

    private val _events = Channel<BookDetailsEvent>(Channel.BUFFERED)
    val events: Flow<BookDetailsEvent> = _events.receiveAsFlow()

    private val uiMode = MutableStateFlow<UiMode>(UiMode.Content)

    private enum class UiMode { Content, Edit }

    private val isSaving = MutableStateFlow(false)
    private val isDeleting = MutableStateFlow(false)

    private val editInputState = MutableStateFlow<BookDetailsEditState?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val bookDataFlow: Flow<BookDetailsDataState> = observeBookSeedSelectionUseCase()
        .flatMapLatest { seed ->
            val currentSeed = seed ?: throw IllegalStateException("No book seed found for details.")

            getBookUseCase(currentSeed.id)
                .combine(observeBookCoverUrlSelectionUseCase())
                { dbBook, selectedCoverUrl -> mapper.mapToDataState(dbBook, selectedCoverUrl) }
        }
        .flowOn(Dispatchers.Default)

    val state: StateFlow<BookDetailsUiState> = combine(
        uiMode,
        bookDataFlow,
        editInputState,
        isSaving,
        isDeleting
    ) { mode, book, editState, saving, deleting ->
        val screenState = when {
            mode == UiMode.Edit && editState != null -> BookDetailsScreenState.Edit(
                isSaving = saving,
                editStateValues = mapper.getEditScreenValues(),
                editState = editState,
                bookData = book
            )

            else -> BookDetailsScreenState.Content(
                isLoading = false,
                isDeleting = deleting,
                bookData = book,
                screenValues = mapper.getScreenValues(),
                deleteDialogValues = mapper.getDeleteDialogValues()
            )
        }
        BookDetailsUiState(screenState)
    }.distinctUntilChanged()
        .flowOn(Dispatchers.Default)
        .catch { _ -> _events.send(BookDetailsEvent.Error(DetailsErrorType.LOADING_BOOK_FAILED)) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            BookDetailsUiState(
                screenState = BookDetailsScreenState.Content(
                    isLoading = true,
                    screenValues = mapper.getScreenValues(),
                    bookData = BookDetailsDataState(
                        id = navSeed?.id ?: "",
                        url = navSeed?.url ?: "",
                        originalUrl = navSeed?.url ?: "",
                        animationKey = navSeed?.animationKey ?: "",
                    ),
                )
            )
        )

    fun markOpen() {
        viewModelScope.launch {
            val bookId = observeBookSeedSelectionUseCase().firstOrNull()?.id ?: return@launch
            markBookAsOpened(bookId)
        }
    }

    fun enterEditMode() {
        val currentBook = (state.value.screenState as? BookDetailsScreenState.Content)?.bookData ?: return
        editInputState.value = BookDetailsEditState(
            editTitle = currentBook.title,
            editAuthors = currentBook.authors,
            editYear = currentBook.year.orEmpty(),
            editIsbn13 = currentBook.isbn13.orEmpty(),
            editIsbn10 = currentBook.isbn10.orEmpty()
        )
        uiMode.value = UiMode.Edit
    }

    fun exitEditMode() {
        uiMode.value = UiMode.Content
        editInputState.value = null
        isSaving.value = false
    }

    fun updateEditTitle(value: String) {
        editInputState.update { it?.copy(editTitle = value, showTitleError = false) }
    }

    fun updateEditAuthors(value: String) {
        editInputState.update { it?.copy(editAuthors = value, showAuthorError = false) }
    }

    fun updateEditPublishedYear(value: String) {
        editInputState.update { it?.copy(editYear = value) }
    }

    fun updateEditIsbn13(value: String) {
        editInputState.update { it?.copy(editIsbn13 = value) }
    }

    fun updateEditIsbn10(value: String) {
        editInputState.update { it?.copy(editIsbn10 = value) }
    }

    fun updateStatus(status: BookReadingStatusUi) {
        val bookId = (state.value.screenState as? BookDetailsScreenState.Content)?.bookData?.id ?: return
        viewModelScope.launch {
            updateBookStatusUseCase(bookId, status.domainStatus)
                .onFailure { _events.send(BookDetailsEvent.Error(DetailsErrorType.UPDATING_STATUS_FAILED)) }
        }
    }

    fun saveEdits() {
        val editState = editInputState.value ?: return
        val book = (state.value.screenState as? BookDetailsScreenState.Edit)?.bookData ?: return

        val isTitleBlank = editState.editTitle.isBlank()
        val isAuthorBlank = editState.editAuthors.isBlank()

        if (isTitleBlank || isAuthorBlank) {
            editInputState.update { it?.copy(showTitleError = isTitleBlank, showAuthorError = isAuthorBlank) }
            return
        }

        viewModelScope.launch {
            isSaving.value = true
            editBookUseCase(
                bookId = book.id,
                title = editState.editTitle,
                authors = editState.editAuthors,
                year = editState.editYear.trim().takeIf { it.isNotEmpty() },
                isbn13 = editState.editIsbn13.trim().takeIf { it.isNotEmpty() },
                isbn10 = editState.editIsbn10.trim().takeIf { it.isNotEmpty() },
                coverUrl = observeBookCoverUrlSelectionUseCase().firstOrNull() ?: book.url,
            ).onSuccess {
                exitEditMode()
            }.onFailure {
                isSaving.value = false
                _events.send(BookDetailsEvent.Error(DetailsErrorType.SAVING_FAILED))
            }
        }
    }

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

            _events.send(BookDetailsEvent.OpenBookCoverPicker)
        }
    }
}
