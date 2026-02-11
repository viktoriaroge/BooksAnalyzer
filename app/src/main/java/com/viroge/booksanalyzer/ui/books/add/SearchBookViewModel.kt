package com.viroge.booksanalyzer.ui.books.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.domain.BookCandidate
import com.viroge.booksanalyzer.domain.BooksUtil.mergeAndRank
import com.viroge.booksanalyzer.domain.SearchMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.skip
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.isNotEmpty

@HiltViewModel
class SearchBookViewModel @Inject constructor(
    private val booksRepo: BooksRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")
    val queryState: StateFlow<String> = query.asStateFlow()

    private val mode = MutableStateFlow(SearchMode.ALL)
    val modeState: StateFlow<SearchMode> = mode.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _canLoadMore = MutableStateFlow(false)
    val canLoadMore: StateFlow<Boolean> = _canLoadMore.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private var nextToken: String? = null
    private var lastQuery: String = ""
    private var currentItems: List<BookCandidate> = emptyList()
    private var lastMessages: List<String> = emptyList()

    init {
        query
            .map { it.trim() }
            .debounce(timeoutMillis = 350)
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
            .debounce(timeoutMillis = 350)
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
                limit = 15,
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
                    SearchUiState.Success(currentItems)

                currentItems.isNotEmpty() && lastMessages.isNotEmpty() ->
                    SearchUiState.Partial(currentItems, lastMessages)

                else ->
                    SearchUiState.Empty(lastQuery)
            }

            _isLoadingMore.value = false
        }
    }

    fun changeQuery(newValue: String) {
        query.value = newValue
    }

    fun changeSearchMode(newMode: SearchMode) {
        mode.value = newMode
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
        lastQuery = q
        nextToken = null
        currentItems = emptyList()
        lastMessages = emptyList()
        _canLoadMore.value = false
        _isLoadingMore.value = false


        _uiState.value = SearchUiState.Loading

        val page = booksRepo.searchPage(
            searchMode = mode.value,
            query = q,
            pageToken = null,
            limit = 15,
        )

        currentItems = page.items
        lastMessages = page.errors.map { it.message ?: it.javaClass.simpleName }
        nextToken = page.nextToken
        _canLoadMore.value = nextToken != null

        _uiState.value = when {
            currentItems.isEmpty() -> SearchUiState.Empty(query = q)
            lastMessages.isEmpty() -> SearchUiState.Success(currentItems)
            else -> SearchUiState.Partial(currentItems, lastMessages)
        }
    }
}
