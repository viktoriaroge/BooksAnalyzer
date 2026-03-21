package com.viroge.booksanalyzer.ui.screens.books.deleted

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.usecase.book.GetRecentlyDeletedBooksUseCase
import com.viroge.booksanalyzer.domain.usecase.book.RestoreBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentlyDeletedViewModel @Inject constructor(
    getRecentlyDeletedBooks: GetRecentlyDeletedBooksUseCase,
    private val restoreBookUseCase: RestoreBookUseCase,
    private val mapper: RecentlyDeletedBookMapper,
) : ViewModel() {

    private val _messages = Channel<String>(Channel.BUFFERED)
    val messages: Flow<String?> = _messages.receiveAsFlow()

    val state: StateFlow<RecentlyDeletedUiState> = getRecentlyDeletedBooks()
        .map { books ->
            RecentlyDeletedUiState(
                screenValues = mapper.getScreenValues(),
                books = mapper.map(books)
            )
        }
        .distinctUntilChanged()
        .flowOn(Dispatchers.Default)
        .catch { _ -> _messages.send("Failed to retrieve Books pending deletion.") } // TODO: Extract string error
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RecentlyDeletedUiState()
        )

    fun restoreBook(bookId: String) {
        viewModelScope.launch {
            restoreBookUseCase(bookId)
                .onFailure { _ -> _messages.send("Failed to restore book.") } // TODO: Extract string error
        }
    }
}
