package com.viroge.booksanalyzer.ui.screens.books.library.collection

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
class CollectionViewModel @Inject constructor(
    private val bookSelectionStateProvider: BookSelectionStateProvider,
    private val observeLibraryDataUseCase: ObserveLibraryDataUseCase,
    private val getBookUseCase: GetBookUseCase,
    private val mapper: CollectionMapper,
) : ViewModel() {

    private val _statusFilter = MutableStateFlow<BookReadingStatusUi?>(value = null) // null == All
    private val _sort: MutableStateFlow<CollectionSortUi> = MutableStateFlow(value = CollectionSortUi.Added)

    private val _query = MutableStateFlow(value = "")
    val query: StateFlow<String> = _query.asStateFlow()

    val filters: StateFlow<CollectionFilters> = combine(
        _statusFilter,
        _sort
    ) { status, sort ->
        CollectionFilters(status, sort)
    }.flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = CollectionFilters()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val screenState: Flow<CollectionScreenState> = combine(
        _query,
        _statusFilter,
        _sort
    ) { q, status, sort ->
        observeLibraryDataUseCase(q, null, sort.domainSource)
            .map { data ->
                val booksWithRequiredStatus =
                    status?.let { data.books.filter { book -> book.status == it.domainStatus } }
                        ?: data.books
                val isLibraryEmpty = data.books.isEmpty()

                CollectionScreenState.Content(
                    stateValues = mapper.getContentStateValues(isLibraryEmpty),
                    filtersSheetValues = mapper.getFiltersSheetValues(),
                    selectedStatus = status,
                    sortState = sort,
                    allBooks = booksWithRequiredStatus.map { mapper.mapToData(it) },

                    isInEmptyState = booksWithRequiredStatus.isEmpty(),
                    showEmptyStateButton = isLibraryEmpty,
                )
            }
    }.flowOn(Dispatchers.Default)
        .flatMapLatest { it }

    val state: StateFlow<CollectionUiState> = screenState
        .map { state ->
            CollectionUiState(
                screenValues = mapper.getScreenValues(),
                screenState = state,
            )
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = CollectionUiState(
                screenValues = CollectionScreenValues(),
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
