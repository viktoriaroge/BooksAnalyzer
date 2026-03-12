package com.viroge.booksanalyzer.ui.screens.books.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.BooksUtil
import com.viroge.booksanalyzer.domain.BooksUtil.mergeAndRank
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchBookViewModel @Inject constructor(
    private val bookSelectionStateProvider: BookSelectionStateProvider,
    private val searchBooksUseCase: SearchBooksUseCase,
    getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val manageHistoryUseCase: ManageSearchHistoryUseCase,
) : ViewModel() {

    val recentQueries: StateFlow<List<String>> = getSearchHistoryUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val query = MutableStateFlow("")
    val queryState = query.asStateFlow()

    private val mode = MutableStateFlow(SearchMode.ALL)
    val modeState = mode.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _canLoadMore = MutableStateFlow(false)
    val canLoadMore = _canLoadMore.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore = _isLoadingMore.asStateFlow()

    private var nextToken: String? = null
    private var lastQuery: String = ""
    private var currentItems: List<Book> = emptyList()
    private var lastMessages: List<String> = emptyList()

    init {
        query.map { it.trim() }
            .onEach { q -> if (q.length < 2) reset() }
            .debounce(800)
            .distinctUntilChanged()
            .onEach { q -> if (q.length >= 2) searchFirstPage(q) }
            .launchIn(viewModelScope)

        mode.drop(1)
            .debounce(500)
            .distinctUntilChanged()
            .onEach { searchFirstPage(query.value) }
            .launchIn(viewModelScope)
    }

    private suspend fun searchFirstPage(q: String) {
        if (q.trim().isEmpty()) return

        lastQuery = q
        nextToken = null
        currentItems = emptyList()
        lastMessages = emptyList()
        _canLoadMore.value = false
        _isLoadingMore.value = false

        onSearchExecuted(q)
        _uiState.value = SearchUiState.Loading

        val result = searchBooksUseCase(q, mode.value, null)

        currentItems = result.items
        lastMessages = result.messages
        nextToken = result.nextToken
        _canLoadMore.value = nextToken != null

        _uiState.value = produceUiState(q)
    }

    fun loadMore() {
        val token = nextToken ?: return
        if (_isLoadingMore.value) return

        viewModelScope.launch {
            _isLoadingMore.value = true

            val result = searchBooksUseCase(lastQuery, mode.value, token)

            currentItems = mergeAndRank(currentItems + result.items)
            lastMessages = (lastMessages + result.messages).distinct()
            nextToken = result.nextToken
            _canLoadMore.value = nextToken != null

            _uiState.value = produceUiState(lastQuery)
            _isLoadingMore.value = false
        }
    }

    private fun produceUiState(q: String): SearchUiState {
        return when {
            currentItems.isEmpty() && lastMessages.isNotEmpty() ->
                SearchUiState.Error(lastMessages.first())

            currentItems.isEmpty() -> SearchUiState.Empty(q)
            lastMessages.isEmpty() -> SearchUiState.Success(q, currentItems)
            else -> SearchUiState.Partial(q, currentItems, lastMessages)
        }
    }

    fun refresh() {
        viewModelScope.launch { searchFirstPage(query.value) }
    }

    fun changeQuery(newValue: String) {
        query.value = newValue
    }

    fun changeSearchMode(newMode: SearchMode) {
        mode.value = newMode
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

    private fun reset() {
        nextToken = null
        lastQuery = ""
        currentItems = emptyList()
        lastMessages = emptyList()
        _canLoadMore.value = false
        _isLoadingMore.value = false
        _uiState.value = SearchUiState.Idle
    }

    fun selectBook(book: Book) {
        val tempBook = TempBook(
            source = book.source,
            sourceId = book.sourceId,
            title = book.title,
            authors = book.authors,
            year = book.publishedYear,
            isbn13 = book.isbn13,
            isbn10 = book.isbn10,
            coverUrl = book.coverUrl,
            coverRequestHeaders = book.coverRequestHeaders,
        )
        bookSelectionStateProvider.selectTempBook(tempBook)
    }

    fun setManualPrefill(query: String, mode: SearchMode) {
        val tempBook = TempBook(
            source = BookSource.MANUAL,
            sourceId = null,
            title = when (mode) {
                SearchMode.ALL,
                SearchMode.TITLE -> BooksUtil.normalizeForManualInput(string = query)

                SearchMode.ISBN,
                SearchMode.AUTHOR -> ""
            },
            authors = when (mode) {
                SearchMode.AUTHOR -> BooksUtil.normalizeForManualInput(string = query)
                    .split(",").map { it.trim() }.filter { it.isNotBlank() }

                SearchMode.ALL,
                SearchMode.TITLE,
                SearchMode.ISBN -> emptyList()
            },
            year = null,
            isbn13 = when (mode) {
                SearchMode.ALL,
                SearchMode.TITLE,
                SearchMode.AUTHOR -> ""

                SearchMode.ISBN -> query
            },
            isbn10 = null,
            coverUrl = null,
            coverRequestHeaders = null,
        )
        bookSelectionStateProvider.selectTempBook(tempBook)
    }
}
