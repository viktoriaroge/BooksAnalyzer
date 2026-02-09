package com.viroge.booksanalyzer.ui.books.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BookSearchViewModel @Inject constructor(
    private val booksRepo: BooksRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")

    // Expose query for TextField
    val queryState: StateFlow<String> = query.asStateFlow()

    fun onQueryChange(newValue: String) {
        query.value = newValue
    }

    // This is the “search as you type” pipeline
    val uiState: StateFlow<SearchUiState> =
        query
            .map { it.trim() }
            .debounce(350) // tweak for taste: 250–450ms
            .distinctUntilChanged()
            .flatMapLatest { query ->
                flow {
                    if (query.isBlank()) {
                        emit(SearchUiState.Idle)
                        return@flow
                    }

                    // Avoid spamming APIs for 1-character queries, at least 2:
                    if (query.length < 2) {
                        emit(SearchUiState.Idle)
                        return@flow
                    }

                    emit(SearchUiState.Loading)

                    when (val res = booksRepo.search(query)) {
                        is BooksRepository.SearchResult.Success ->
                            if (res.items.isEmpty()) emit(SearchUiState.Empty(query))
                            else emit(SearchUiState.Success(res.items))

                        is BooksRepository.SearchResult.Partial -> {
                            val msgs = res.errors.map { it.message ?: it.javaClass.simpleName }
                            emit(SearchUiState.Partial(res.items, msgs))
                        }

                        is BooksRepository.SearchResult.Failure -> {
                            val msg = res.errors.firstOrNull()?.message ?: "Search failed"
                            emit(SearchUiState.Error(msg))
                        }
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SearchUiState.Idle
            )
}
