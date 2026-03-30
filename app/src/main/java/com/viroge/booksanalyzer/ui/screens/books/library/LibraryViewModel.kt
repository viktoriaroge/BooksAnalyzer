package com.viroge.booksanalyzer.ui.screens.books.library

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.model.BookSeed
import com.viroge.booksanalyzer.domain.usecase.book.GetBookUseCase
import com.viroge.booksanalyzer.domain.usecase.book.LibrarySort
import com.viroge.booksanalyzer.domain.usecase.book.ObserveLibraryDataUseCase
import com.viroge.booksanalyzer.domain.usecase.selection.SelectBookCoverUrlUseCase
import com.viroge.booksanalyzer.domain.usecase.selection.SelectBookSeedUseCase
import com.viroge.booksanalyzer.ui.nav.StateArguments
import com.viroge.booksanalyzer.ui.screens.books.BookTransitionKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val selectBookCoverUrlUseCase: SelectBookCoverUrlUseCase,
    private val selectBookSeedUseCase: SelectBookSeedUseCase,
    observeLibraryDataUseCase: ObserveLibraryDataUseCase,
    private val getBookUseCase: GetBookUseCase,
    private val mapper: LibraryMapper,
) : ViewModel() {

    private val transitionPref: String = savedStateHandle[StateArguments.TRANSITION_PREFIX_ARG] ?: ""

    private val _events = Channel<LibraryEvent>(Channel.BUFFERED)
    val events: Flow<LibraryEvent> = _events.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val screenState: Flow<LibraryScreenState> =
        observeLibraryDataUseCase("", null, LibrarySort.RECENT)
            .map { data ->
                when {
                    data.books.isEmpty() -> LibraryScreenState.Empty(
                        navRoute = LibraryNavDirection.SEARCH,
                        emptyStateValues = mapper.getEmptyStateValues(),
                    )

                    data.currentlyReading.isEmpty() -> LibraryScreenState.Empty(
                        navRoute = LibraryNavDirection.COLLECTION,
                        emptyStateValues = mapper.getEmptyStateNoCurrentsValues(),
                    )

                    else -> LibraryScreenState.Content(
                        contentStateValues = mapper.getContentStateValues(),
                        currentBooks = data.currentlyReading.map { mapper.mapToData(it, transitionPref) },
                    )
                }
            }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)

    val state: StateFlow<LibraryUiState> = screenState
        .map { state ->
            LibraryUiState(
                screenValues = mapper.getScreenValues(),
                screenState = state,
            )
        }
        .distinctUntilChanged()
        .flowOn(Dispatchers.Default)
        .catch { _ -> Log.e("LibraryViewModel", "Failed to prepare ui state.") } // TODO: Send error to UI
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = LibraryUiState(
                screenValues = LibraryScreenValues(),
                screenState = LibraryScreenState.Loading,
            )
        )

    fun onOpenBook(bookId: String) {
        viewModelScope.launch {
            val book = getBookUseCase(bookId) ?: return@launch

            val seed = BookSeed(
                id = book.id,
                url = book.coverUrl ?: "",
                animationKey = BookTransitionKey.calculate(
                    transitionPref = transitionPref,
                    title = book.title,
                    authors = book.authors,
                    isbn = book.isbn13,
                    source = book.source,
                    sourceId = book.sourceId,
                )
            )
            selectBookSeedUseCase(seed)
            selectBookCoverUrlUseCase(null)

            _events.send(LibraryEvent.OpenBookDetails(seed))
        }
    }
}
