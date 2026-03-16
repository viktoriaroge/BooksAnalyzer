package com.viroge.booksanalyzer.ui.screens.books.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.provider.BookSelectionStateProvider
import com.viroge.booksanalyzer.domain.provider.CoverPickerStateProvider
import com.viroge.booksanalyzer.domain.usecase.EditBookUseCase
import com.viroge.booksanalyzer.domain.usecase.MarkBookAsOpenedUseCase
import com.viroge.booksanalyzer.domain.usecase.ObserveBookUseCase
import com.viroge.booksanalyzer.domain.usecase.UpdateBookStatusUseCase
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val bookSelectionStateProvider: BookSelectionStateProvider,
    private val coverPickerStateProvider: CoverPickerStateProvider,
    private val getBookUseCase: ObserveBookUseCase,
    private val markBookAsOpened: MarkBookAsOpenedUseCase,
    private val updateBookStatusUseCase: UpdateBookStatusUseCase,
    private val editBookUseCase: EditBookUseCase,
    private val mapper: BookDetailsMapper,
) : ViewModel() {

    private val _events = MutableSharedFlow<BookDetailsEvent>()
    val events = _events.asSharedFlow()

    private var needsMarking = true

    private val uiMode = MutableStateFlow<UiMode>(UiMode.Content)

    private enum class UiMode { Content, Edit }

    private val isSaving = MutableStateFlow(false)
    private val isDeleting = MutableStateFlow(false)

    private val editInputState = MutableStateFlow<BookDetailsEditState?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val bookDataFlow: Flow<BookDetailsDataState> = bookSelectionStateProvider.selectedBookSeed
        .flatMapLatest { seed ->
            val currentSeed = seed ?: throw IllegalStateException("No book seed found for details.")

            getBookUseCase(currentSeed.id)
                .combine(coverPickerStateProvider.state) { dbBook, pickerState ->
                    mapper.mapToDataState(dbBook, pickerState.selectedCandidate)
                }
        }
        .onEach { book ->
            if (needsMarking) {
                markBookAsOpened(book.id)
                needsMarking = false
            }
        }
        .catch { _ -> _events.emit(BookDetailsEvent.Error(DetailsErrorType.LOADING_BOOK_FAILED)) }
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
    }.flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            BookDetailsUiState(
                screenState = BookDetailsScreenState.Content(
                    isLoading = true,
                    screenValues = mapper.getScreenValues(),
                    bookData = BookDetailsDataState(
                        id = bookSelectionStateProvider.getSelectedBookSeed()?.id ?: "",
                        url = bookSelectionStateProvider.getSelectedBookSeed()?.url ?: "",
                        headers = bookSelectionStateProvider.getSelectedBookSeed()?.headers ?: emptyMap(),
                        animationKey = bookSelectionStateProvider.getSelectedBookSeed()?.animationKey ?: "",
                    ),
                )
            )
        )

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
                .onFailure { _events.emit(BookDetailsEvent.Error(DetailsErrorType.UPDATING_STATUS_FAILED)) }
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
                coverUrl = coverPickerStateProvider.state.value.selectedCandidate?.url ?: book.url,
            ).onSuccess {
                exitEditMode()
            }.onFailure {
                isSaving.value = false
                _events.emit(BookDetailsEvent.Error(DetailsErrorType.SAVING_FAILED))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        coverPickerStateProvider.clear()
        bookSelectionStateProvider.clearSelection()
    }
}
