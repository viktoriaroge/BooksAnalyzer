package com.viroge.booksanalyzer.ui.screens.books.recentlydeleted

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.usecase.GetRecentlyDeletedBooksUseCase
import com.viroge.booksanalyzer.domain.usecase.RestoreBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentlyDeletedViewModel @Inject constructor(
    getRecentlyDeletedBooks: GetRecentlyDeletedBooksUseCase,
    private val restoreBookUseCase: RestoreBookUseCase,
) : ViewModel() {

    private val _message = MutableSharedFlow<String?>()
    val message: SharedFlow<String?> = _message

    val state: StateFlow<RecentlyDeletedUiState> = getRecentlyDeletedBooks()
        .map { RecentlyDeletedUiState(books = it) }
        .distinctUntilChanged()
        .catch { _ -> _message.emit("Failed to retrieve Books pending deletion.") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RecentlyDeletedUiState()
        )

    fun restoreBook(bookId: String) {
        viewModelScope.launch {
            restoreBookUseCase(bookId)
                .onFailure { _ -> _message.emit("Failed to restore book.") }
        }
    }
}

data class RecentlyDeletedUiState(
    val books: List<Book> = emptyList(),
)
