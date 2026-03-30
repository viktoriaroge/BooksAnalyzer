package com.viroge.booksanalyzer.ui.screens.books.library.collection

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.model.BookSeed
import com.viroge.booksanalyzer.domain.usecase.book.GetBookUseCase
import com.viroge.booksanalyzer.domain.usecase.book.ObserveHasAvailableBooksUseCase
import com.viroge.booksanalyzer.domain.usecase.book.ObserveLibraryDataUseCase
import com.viroge.booksanalyzer.domain.usecase.selection.SelectBookCoverUrlUseCase
import com.viroge.booksanalyzer.domain.usecase.selection.SelectBookSeedUseCase
import com.viroge.booksanalyzer.ui.nav.StateArguments
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import com.viroge.booksanalyzer.ui.screens.books.BookTransitionKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val selectBookCoverUrlUseCase: SelectBookCoverUrlUseCase,
    private val selectBookSeedUseCase: SelectBookSeedUseCase,
    private val observeLibraryDataUseCase: ObserveLibraryDataUseCase,
    observeHasAvailableBooksUseCase: ObserveHasAvailableBooksUseCase,
    private val getBookUseCase: GetBookUseCase,
    private val mapper: CollectionMapper,
) : ViewModel() {

    private val transitionPref: String = savedStateHandle[StateArguments.TRANSITION_PREFIX_ARG] ?: ""

    private val _events = Channel<CollectionEvent>(Channel.BUFFERED)
    val events: Flow<CollectionEvent> = _events.receiveAsFlow()

    private val _statusFilter = MutableStateFlow<BookReadingStatusUi?>(value = null) // null == All
    private val _sort: MutableStateFlow<CollectionSortUi> = MutableStateFlow(value = CollectionSortUi.Added)

    private val _query = MutableStateFlow(value = "")
    val query: StateFlow<String> = _query.asStateFlow()

    val filters: StateFlow<CollectionFilters> = combine(
        _statusFilter,
        _sort
    ) { status, sort -> CollectionFilters(status, sort) }
        .flowOn(Dispatchers.Default)
        .catch { _ -> Log.e("CollectionViewModel", "Failed to prepare filters.") } // TODO: Send error to UI
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = CollectionFilters()
        )

    private val hasBooks = observeHasAvailableBooksUseCase()
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val screenState: Flow<CollectionScreenState> = combine(
        _query.debounce(300),
        _statusFilter,
        _sort,
        hasBooks
    ) { q, status, sort, hasAnyBooks ->
        observeLibraryDataUseCase(q, status?.domainStatus, sort.domainSource)
            .map { data ->
                CollectionScreenState.Content(
                    stateValues = mapper.getContentStateValues(!hasAnyBooks),
                    filtersSheetValues = mapper.getFiltersSheetValues(),
                    selectedStatus = status,
                    sortState = sort,
                    // TODO: Implement paging, this can easily get out of hand:
                    allBooks = data.books.map { mapper.mapToData(it, transitionPref) },

                    isInEmptyState = data.books.isEmpty(),
                    showEmptyStateButton = !hasAnyBooks,
                )
            }
    }.distinctUntilChanged()
        .flowOn(Dispatchers.Default)
        .flatMapLatest { it }

    val state: StateFlow<CollectionUiState> = screenState
        .map { state ->
            CollectionUiState(
                screenValues = mapper.getScreenValues(),
                screenState = state,
            )
        }
        .distinctUntilChanged()
        .flowOn(Dispatchers.Default)
        .catch { _ -> Log.e("CollectionViewModel", "Failed to prepare ui state.") } // TODO: Send error to UI
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = CollectionUiState(
                screenValues = mapper.getScreenValues(),
                screenState = CollectionScreenState.Loading,
            )
        )

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun onStatusChange(status: BookReadingStatusUi?) {
        _statusFilter.value = status
    }

    fun onSortChange(newSort: CollectionSortUi) {
        _sort.value = newSort
    }

    fun onClearFilters() {
        _statusFilter.value = null
        _sort.value = CollectionSortUi.Added
    }

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

            _events.send(CollectionEvent.OpenBookDetails(seed))
        }
    }
}
