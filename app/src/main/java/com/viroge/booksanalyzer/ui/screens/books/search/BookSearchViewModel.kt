package com.viroge.booksanalyzer.ui.screens.books.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.BooksUtil.mergeAndRank
import com.viroge.booksanalyzer.domain.model.SearchMode
import com.viroge.booksanalyzer.domain.model.TempBook
import com.viroge.booksanalyzer.domain.provider.BookSelectionStateProvider
import com.viroge.booksanalyzer.domain.usecase.GetSearchHistoryUseCase
import com.viroge.booksanalyzer.domain.usecase.ManageSearchHistoryUseCase
import com.viroge.booksanalyzer.domain.usecase.SearchBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class BookSearchViewModel @Inject constructor(
    private val bookSelectionStateProvider: BookSelectionStateProvider,
    private val searchBooksUseCase: SearchBooksUseCase,
    getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val manageHistoryUseCase: ManageSearchHistoryUseCase,
    private val mapper: BookSearchMapper,
) : ViewModel() {

    private val _screenState: MutableStateFlow<SearchScreenState> = MutableStateFlow(
        SearchScreenState.Idle(
            recentSearchesValues = mapper.getRecentSearchesValues(),
            searchHistoryDialogValues = mapper.getSearchHistoryDialogValues(),
        )
    )

    private val _loadingIndicator: MutableStateFlow<LoadingIndicator> = MutableStateFlow(
        LoadingIndicator(
            canLoadMore = false,
            isLoadingMore = false,
        )
    )
    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    private val _mode: MutableStateFlow<SearchMode> = MutableStateFlow(SearchMode.ALL)

    val state: StateFlow<BookSearchUiState> = combine(
        getSearchHistoryUseCase(),
        _screenState,
        _loadingIndicator,
        _query,
        _mode
    ) { recent, screenState, loadingIndicator, query, mode ->
        BookSearchUiState(
            isLoadingMore = loadingIndicator.isLoadingMore,
            canLoadMore = loadingIndicator.canLoadMore,
            query = query,
            mode = mode,
            recent = recent,
            screenState = screenState,
            screenValues = mapper.getScreenValues(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BookSearchUiState(
            isLoadingMore = false,
            canLoadMore = false,
            query = "",
            mode = SearchMode.ALL,
            recent = emptyList(),
            screenState = SearchScreenState.Idle(
                recentSearchesValues = mapper.getRecentSearchesValues(),
                searchHistoryDialogValues = mapper.getSearchHistoryDialogValues()
            ),
            screenValues = mapper.getScreenValues(),
        )
    )

    private var nextToken: String? = null
    private var lastQuery: String = ""
    private var currentItems: List<TempBook> = emptyList()
    private var lastMessages: List<String> = emptyList()

    init {
        _query.map { it.trim() }
            .onEach { q -> if (q.length < 2) reset() }
            .debounce(800)
            .distinctUntilChanged()
            .onEach { q -> if (q.length >= 2) searchFirstPage(q) }
            .launchIn(viewModelScope)

        _mode.drop(1)
            .debounce(500)
            .distinctUntilChanged()
            .onEach { searchFirstPage(_query.value) }
            .launchIn(viewModelScope)
    }

    fun refresh() {
        viewModelScope.launch { searchFirstPage(_query.value) }
    }

    fun changeQuery(newValue: String) {
        _query.value = newValue
    }

    fun changeSearchMode(newMode: SearchMode) {
        _mode.value = newMode
    }

    private fun onSearchExecuted(q: String) {
        viewModelScope.launch { manageHistoryUseCase.record(q) }
    }

    fun removeRecent(q: String) {
        viewModelScope.launch { manageHistoryUseCase.delete(q) }
    }

    fun clearRecents() {
        viewModelScope.launch { manageHistoryUseCase.clearAll() }
    }

    fun selectBook(book: SearchBookDataState) {
        bookSelectionStateProvider.selectTempBook(mapper.mapToTempBook(book))
    }

    fun setManualPrefill(
        query: String,
        mode: SearchMode,
    ) {
        bookSelectionStateProvider.selectTempBook(mapper.mapToTempBook(query, mode))
    }

    fun loadMore() {
        val token = nextToken ?: return
        if (_loadingIndicator.value.isLoadingMore) return

        viewModelScope.launch {
            _loadingIndicator.update {
                it.copy(
                    isLoadingMore = true,
                )
            }

            val result = searchBooksUseCase(lastQuery, _mode.value, token)

            currentItems = mergeAndRank(currentItems + result.items)
            lastMessages = (lastMessages + result.messages).distinct()
            nextToken = result.nextToken
            _loadingIndicator.update {
                it.copy(
                    canLoadMore = nextToken != null,
                )
            }

            _screenState.value = produceUiState(lastQuery)
            _loadingIndicator.update {
                it.copy(
                    isLoadingMore = false,
                )
            }
        }
    }

    private suspend fun searchFirstPage(q: String) {
        if (q.trim().isEmpty()) return

        lastQuery = q
        nextToken = null
        currentItems = emptyList()
        lastMessages = emptyList()
        _loadingIndicator.update {
            it.copy(
                canLoadMore = false,
                isLoadingMore = false,
            )
        }

        onSearchExecuted(q)
        _screenState.value = SearchScreenState.Loading

        val result = searchBooksUseCase(q, _mode.value, null)

        currentItems = result.items
        lastMessages = result.messages
        nextToken = result.nextToken
        _loadingIndicator.update {
            it.copy(
                canLoadMore = nextToken != null,
            )
        }

        _screenState.value = produceUiState(q)
    }

    private fun produceUiState(q: String): SearchScreenState = when {
        currentItems.isEmpty() && lastMessages.isNotEmpty() ->
            SearchScreenState.Error(
                message = lastMessages.first(),
                errorStateValues = mapper.getErrorStateValues(),
            )

        currentItems.isEmpty() -> SearchScreenState.Empty(
            query = q,
            emptyStateValues = mapper.getEmptyStateValues(),
        )

        lastMessages.isEmpty() -> SearchScreenState.Success(
            query = q,
            items = currentItems.map { mapper.mapToDataState(it) },
            contentStateValues = mapper.getContentStateValues(),
        )

        else -> SearchScreenState.Partial(
            query = q,
            items = currentItems.map { mapper.mapToDataState(it) },
            messages = lastMessages,
            contentStateValues = mapper.getContentStateValues(),
        )
    }

    private fun reset() {
        nextToken = null
        lastQuery = ""
        currentItems = emptyList()
        lastMessages = emptyList()
        _loadingIndicator.update {
            it.copy(
                canLoadMore = false,
                isLoadingMore = false,
            )
        }
        _screenState.value = SearchScreenState.Idle(
            recentSearchesValues = mapper.getRecentSearchesValues(),
            searchHistoryDialogValues = mapper.getSearchHistoryDialogValues(),
        )
    }
}

data class LoadingIndicator(
    val canLoadMore: Boolean,
    val isLoadingMore: Boolean,
)
