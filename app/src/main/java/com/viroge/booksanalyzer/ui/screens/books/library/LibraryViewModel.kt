package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.provider.BookSelectionStateProvider
import com.viroge.booksanalyzer.domain.usecase.ObserveLibraryDataUseCase
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
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
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val bookSelectionStateProvider: BookSelectionStateProvider,
    private val observeLibraryDataUseCase: ObserveLibraryDataUseCase,
    private val mapper: LibraryMapper,
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
    private val screenState: Flow<LibraryScreenState> = combine(
        _query,
        _statusFilter,
        _sort
    ) { q, status, sort ->
        observeLibraryDataUseCase(q, status?.domainStatus, sort.domainSource)
            .map { data ->
                when {
                    data.books.isEmpty() -> LibraryScreenState.Empty(
                        emptyStateValues = mapper.getEmptyStateValues(),
                    )

                    else -> LibraryScreenState.Content(
                        contentStateValues = mapper.getContentStateValues(),
                        filtersSheetValues = mapper.getFiltersSheetValues(),
                        selectedStatus = status,
                        sortState = sort,
                        currentBooks = data.currentlyReading.map { mapper.mapToData(it) },
                        allBooks = data.books.map { mapper.mapToData(it) },
                    )
                }
            }
    }.flowOn(Dispatchers.Default)
        .flatMapLatest { it }

    val state: StateFlow<LibraryUiState> = screenState
        .map { state ->
            LibraryUiState(
                screenValues = mapper.getScreenValues(),
                screenState = state,
            )
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = LibraryUiState(
                screenValues = LibraryScreenValues(),
                screenState = LibraryScreenState.Loading,
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
        bookSelectionStateProvider.selectBookId(bookId)
    }
}
