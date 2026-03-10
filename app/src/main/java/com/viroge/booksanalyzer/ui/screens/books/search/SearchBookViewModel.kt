package com.viroge.booksanalyzer.ui.screens.books.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.data.SearchHistoryRepository
import com.viroge.booksanalyzer.domain.BooksUtil
import com.viroge.booksanalyzer.domain.BooksUtil.mergeAndRank
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.TempBook
import com.viroge.booksanalyzer.domain.model.library.SearchMode
import com.viroge.booksanalyzer.domain.provider.BookSelectionStateProvider
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
    private val booksRepo: BooksRepository,
    private val historyRepo: SearchHistoryRepository,
) : ViewModel() {

    val recentQueries: StateFlow<List<String>> =
        historyRepo.observeRecent(limit = 10)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = emptyList(),
            )

    private val query = MutableStateFlow(value = "")
    val queryState: StateFlow<String> = query.asStateFlow()

    private val mode = MutableStateFlow(value = SearchMode.ALL)
    val modeState: StateFlow<SearchMode> = mode.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(value = SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _canLoadMore = MutableStateFlow(value = false)
    val canLoadMore: StateFlow<Boolean> = _canLoadMore.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(value = false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private var nextToken: String? = null
    private var lastQuery: String = ""
    private var currentItems: List<Book> = emptyList()
    private var lastMessages: List<String> = emptyList()

    init {
        query
            .map { it.trim() }
            .debounce(timeoutMillis = 550)
            .distinctUntilChanged()
            .onEach { q ->
                if (q.isBlank() || q.length < 2) {
                    reset()
                    return@onEach
                }
                searchFirstPage(q)
            }
            .launchIn(viewModelScope)

        mode
            .drop(count = 1)
            .debounce(timeoutMillis = 450)
            .distinctUntilChanged()
            .onEach { searchFirstPage(query.value) }
            .launchIn(viewModelScope)
    }

    fun loadMore() {
        val token = nextToken ?: return
        if (_isLoadingMore.value) return

        viewModelScope.launch {
            _isLoadingMore.value = true

            val page = booksRepo.searchPage(
                searchMode = mode.value,
                query = lastQuery,
                pageToken = token,
            )

            // append + dedupe
            currentItems = mergeAndRank((currentItems + page.items))

            lastMessages = (lastMessages + page.errors
                .map { it.message ?: it.javaClass.simpleName })
                .distinct()

            nextToken = page.nextToken
            _canLoadMore.value = nextToken != null

            _uiState.value = when {
                currentItems.isNotEmpty() && lastMessages.isEmpty() ->
                    SearchUiState.Success(query = lastQuery, items = currentItems)

                currentItems.isNotEmpty() && lastMessages.isNotEmpty() ->
                    SearchUiState.Partial(
                        query = lastQuery,
                        items = currentItems,
                        messages = lastMessages
                    )

                else ->
                    SearchUiState.Empty(lastQuery)
            }

            _isLoadingMore.value = false
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val currentValueToRefresh = query.value
            searchFirstPage(currentValueToRefresh)
        }
    }

    fun changeQuery(newValue: String) {
        query.value = newValue
    }

    fun changeSearchMode(newMode: SearchMode) {
        mode.value = newMode
    }

    fun onSearchExecuted(query: String) {
        viewModelScope.launch {
            historyRepo.recordQuery(
                query = query,
                limit = 10,
            )
        }
    }

    fun removeRecent(query: String) {
        viewModelScope.launch { historyRepo.deleteQuery(query) }
    }

    fun clearRecents() {
        viewModelScope.launch { historyRepo.clearAll() }
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

    private suspend fun searchFirstPage(q: String) {
        if (q.trim().isEmpty()) return

        lastQuery = q
        nextToken = null
        currentItems = emptyList()
        lastMessages = emptyList()
        _canLoadMore.value = false
        _isLoadingMore.value = false

        onSearchExecuted(query = q)

        _uiState.value = SearchUiState.Loading

        val page = booksRepo.searchPage(
            searchMode = mode.value,
            query = q,
            pageToken = null,
        )

        currentItems = page.items
        lastMessages = page.errors.map { it.message ?: it.javaClass.simpleName }
        nextToken = page.nextToken
        _canLoadMore.value = nextToken != null

        _uiState.value = when {
            currentItems.isEmpty() && lastMessages.isNotEmpty() -> {
                val message = lastMessages.first()
                val normalizedMessage = if (message.lowercase().contains("noconnection")) "Check your Internet connection." else message
                SearchUiState.Error(message = normalizedMessage)
            }

            currentItems.isEmpty() -> SearchUiState.Empty(query = q)
            lastMessages.isEmpty() -> SearchUiState.Success(query = q, items = currentItems)
            else -> SearchUiState.Partial(
                query = q,
                items = currentItems,
                messages = lastMessages,
            )
        }
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

sealed interface SearchUiState {

    data object Idle : SearchUiState
    data object Loading : SearchUiState

    data class Success(
        val query: String,
        val items: List<Book>,
    ) : SearchUiState

    data class Partial(
        val query: String,
        val items: List<Book>,
        val messages: List<String>,
    ) : SearchUiState

    data class Empty(
        val query: String,
    ) : SearchUiState

    data class Error(
        val message: String,
    ) : SearchUiState
}
