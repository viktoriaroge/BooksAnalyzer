package com.viroge.booksanalyzer.ui.screens.books.library.full

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.provider.BookSelectionStateProvider
import com.viroge.booksanalyzer.domain.usecase.book.GetBookUseCase
import com.viroge.booksanalyzer.domain.usecase.book.ObserveLibraryDataUseCase
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import com.viroge.booksanalyzer.ui.screens.books.BookTransitionKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryFullCollectionViewModel @Inject constructor(
    private val bookSelectionStateProvider: BookSelectionStateProvider,
    private val observeLibraryDataUseCase: ObserveLibraryDataUseCase,
    private val getBookUseCase: GetBookUseCase,
    private val mapper: LibraryFullCollectionMapper,
) : ViewModel() {

    private val _statusFilter = MutableStateFlow<BookReadingStatusUi?>(value = null) // null == All
    private val _sort: MutableStateFlow<LibrarySortUi> = MutableStateFlow(value = LibrarySortUi.Added)

    private val _query = MutableStateFlow(value = "")
    val query: StateFlow<String> = _query.asStateFlow()

    val filters: StateFlow<LibraryFilters> = combine(
        _statusFilter,
        _sort
    ) { status, sort ->
        LibraryFilters(status, sort)
    }.flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = LibraryFilters()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val screenState: Flow<LibraryFullCollectionScreenState> = combine(
        _query,
        _statusFilter,
        _sort
    ) { q, status, sort ->
        observeLibraryDataUseCase(q, status?.domainStatus, sort.domainSource)
            .map { data ->
                LibraryFullCollectionScreenState.Content(
                    fullCollectionStateValues = mapper.getContentStateValues(),
                    filtersSheetValues = mapper.getFiltersSheetValues(),
                    selectedStatus = status,
                    sortState = sort,
                    allBooks = data.books.map { mapper.mapToData(it) },
                )
            }
    }.flowOn(Dispatchers.Default)
        .flatMapLatest { it }

    val state: StateFlow<LibraryFullCollectionUiState> = screenState
        .map { state ->
            LibraryFullCollectionUiState(
                screenValues = mapper.getScreenValues(),
                screenState = state,
            )
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = LibraryFullCollectionUiState(
                screenValues = LibraryFullCollectionScreenValues(),
                screenState = LibraryFullCollectionScreenState.Loading,
            )
        )

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun onStatusChange(status: BookReadingStatusUi?) {
        _statusFilter.value = status
    }

    fun onSortChange(newSort: LibrarySortUi) {
        _sort.value = newSort
    }

    fun onClearFilters() {
        _statusFilter.value = null
        _sort.value = LibrarySortUi.Added
    }

    fun selectBook(bookId: String) {
        viewModelScope.launch {
            getBookUseCase(bookId)?.let { book ->
                bookSelectionStateProvider.selectBookSeed(
                    bookId = book.id,
                    bookCoverUrl = book.coverUrl ?: "",
                    bookCoverRequestHeaders = book.coverRequestHeaders,
                    bookAnimationKey = BookTransitionKey.calculate(
                        title = book.title,
                        authors = book.authors,
                        isbn = book.isbn13,
                        source = book.source,
                        sourceId = book.sourceId,
                    )
                )
            }
        }
    }
}
