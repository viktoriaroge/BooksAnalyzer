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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class BookSearchViewModel @Inject constructor(
    private val bookSelectionStateProvider: BookSelectionStateProvider,
    private val searchBooksUseCase: SearchBooksUseCase,
    getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val manageHistoryUseCase: ManageSearchHistoryUseCase,
    private val mapper: BookSearchMapper,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _mode = MutableStateFlow(SearchMode.ALL)
    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
        .apply { tryEmit(Unit) }

    private val _currentItems = MutableStateFlow<List<TempBook>>(emptyList())
    private val _lastMessages = MutableStateFlow<List<String>>(emptyList())
    private val _nextToken = MutableStateFlow<String?>(null)
    private val _loadingIndicator = MutableStateFlow(LoadingIndicator(canLoadMore = false, isLoadingMore = false))

    private var lastQuery: String = ""
    private var lastMode: SearchMode? = null

    private val screenPhase: Flow<SearchPhase> = combine(
        _query.map { it.trim() }.distinctUntilChanged(),
        _mode,
        _refreshTrigger
    ) { q, mode, _ -> q to mode }
        .debounce { (q, _) -> if (q.isEmpty()) 0L else 800L }
        .flatMapLatest { (q, mode) ->
            flow {
                if (q.length < 2) {
                    resetInternalData()
                    lastQuery = ""
                    lastMode = null
                    emit(SearchPhase.Idle)
                    return@flow
                }

                if (q == lastQuery && mode == lastMode && _currentItems.value.isNotEmpty()) {
                    emit(SearchPhase.DisplayingResults)
                    return@flow
                }

                emit(SearchPhase.Loading)


                val result = searchBooksUseCase(q, mode, null)

                lastQuery = q
                lastMode = mode
                _currentItems.value = result.items
                _lastMessages.value = result.messages
                _nextToken.value = result.nextToken

                onSearchExecuted(q)
                emit(SearchPhase.DisplayingResults)
            }
        }

    val state: StateFlow<BookSearchUiState> = combine(
        getSearchHistoryUseCase(),     // 1
        _query,                                 // 2
        _mode,                                  // 3
        screenPhase,                            // 4
        _currentItems,                          // 5
        _lastMessages,                          // 6
        _loadingIndicator                       // 7
    ) { args ->
        // Combining more than 5 -> arguments come in as an Array<Any> in the order they were passed:
        val recent = args[0] as List<String>
        val q = args[1] as String
        val mode = args[2] as SearchMode
        val phase = args[3] as SearchPhase
        val items = args[4] as List<TempBook>
        val messages = args[5] as List<String>
        val loading = args[6] as LoadingIndicator

        val screenState = when (phase) {
            SearchPhase.Idle -> SearchScreenState.Idle(
                recentSearchesValues = mapper.getRecentSearchesValues(),
                searchHistoryDialogValues = mapper.getSearchHistoryDialogValues()
            )

            SearchPhase.Loading -> SearchScreenState.Loading
            SearchPhase.DisplayingResults -> produceResultState(q, items, messages)
        }

        BookSearchUiState(
            isLoadingMore = loading.isLoadingMore,
            canLoadMore = _nextToken.value != null,
            query = q,
            mode = BookSearchModeUi.fromDomain(mode),
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
            mode = BookSearchModeUi.All,
            recent = emptyList(),
            screenState = SearchScreenState.Idle(
                mapper.getRecentSearchesValues(),
                mapper.getSearchHistoryDialogValues()
            ),
            screenValues = mapper.getScreenValues()
        )
    )

    fun changeQuery(newValue: String) {
        _query.value = newValue
    }

    fun changeSearchMode(newMode: BookSearchModeUi) {
        _mode.value = newMode.domainStatus
    }

    fun refresh() {
        _refreshTrigger.tryEmit(Unit)
    }

    fun loadMore() {
        val q = _query.value.trim()
        val token = _nextToken.value ?: return
        if (_loadingIndicator.value.isLoadingMore || q.length < 2) return

        viewModelScope.launch {
            _loadingIndicator.update { it.copy(isLoadingMore = true) }

            val result = searchBooksUseCase(q, _mode.value, token)

            _currentItems.value = mergeAndRank(_currentItems.value + result.items)
            _lastMessages.value = (_lastMessages.value + result.messages).distinct()
            _nextToken.value = result.nextToken

            _loadingIndicator.update { it.copy(isLoadingMore = false) }
        }
    }

    private fun resetInternalData() {
        _currentItems.value = emptyList()
        _lastMessages.value = emptyList()
        _nextToken.value = null
        _loadingIndicator.update { it.copy(isLoadingMore = false, canLoadMore = false) }
    }

    private fun produceResultState(q: String, items: List<TempBook>, messages: List<String>): SearchScreenState {
        return when {
            items.isEmpty() && messages.isNotEmpty() -> SearchScreenState.Error(
                message = messages.first(),
                errorStateValues = mapper.getErrorStateValues()
            )

            items.isEmpty() -> SearchScreenState.Empty(
                query = q,
                emptyStateValues = mapper.getEmptyStateValues()
            )

            messages.isEmpty() -> SearchScreenState.Success(
                query = q,
                items = items.map { mapper.mapToDataState(it) },
                contentStateValues = mapper.getContentStateValues()
            )

            else -> SearchScreenState.Partial(
                query = q,
                items = items.map { mapper.mapToDataState(it) },
                messages = messages,
                contentStateValues = mapper.getContentStateValues()
            )
        }
    }

    private fun onSearchExecuted(q: String) {
        viewModelScope.launch { manageHistoryUseCase.record(q) }
    }

    fun removeRecent(q: String) = viewModelScope.launch { manageHistoryUseCase.delete(q) }

    fun clearRecents() = viewModelScope.launch { manageHistoryUseCase.clearAll() }

    fun selectBook(book: SearchBookDataState) {
        bookSelectionStateProvider.selectTempBook(mapper.mapToTempBook(book))
    }

    fun setManualPrefill(query: String, mode: BookSearchModeUi) {
        bookSelectionStateProvider.selectTempBook(mapper.mapToTempBook(query, mode.domainStatus))
    }

    private enum class SearchPhase { Idle, Loading, DisplayingResults }
}

data class LoadingIndicator(
    val canLoadMore: Boolean,
    val isLoadingMore: Boolean,
)
