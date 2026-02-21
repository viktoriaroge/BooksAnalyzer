package com.viroge.booksanalyzer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.domain.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainSharedViewModel @Inject constructor(
    private val repo: BooksRepository,
) : ViewModel() {

    private val _events = MutableSharedFlow<AppEvent>()
    val events: SharedFlow<AppEvent> = _events

    private var lastDeletedBook: Book? = null

    fun delete(book: Book) {
        viewModelScope.launch {
            runCatching { repo.markBookToDelete(book.id) }
                .onSuccess { deletedBookData ->
                    lastDeletedBook = book

                    if (deletedBookData != null) {
                        _events.emit(AppEvent.BookDeleted(id = book.id, title = book.title))
                    }
                }
                .onFailure { _ -> _events.emit(AppEvent.BookDeletingFailed(title = book.title)) }
        }
    }

    fun undoDelete() {
        val toRestoreBook = lastDeletedBook ?: return

        viewModelScope.launch {
            runCatching { repo.restore(bookId = toRestoreBook.id) }
                .onSuccess {
                    lastDeletedBook = null
                    _events.emit(AppEvent.BookRestoreSuccess(title = toRestoreBook.title))
                }
                .onFailure { _ -> _events.emit(AppEvent.BookRestoreFailed(title = toRestoreBook.title)) }
        }
    }
}

sealed interface AppEvent {

    data class BookDeleted(
        val id: String,
        val title: String,
    ) : AppEvent

    data class BookDeletingFailed(
        val title: String,
    ) : AppEvent

    data class BookRestoreSuccess(
        val title: String,
    ) : AppEvent

    data class BookRestoreFailed(
        val title: String,
    ) : AppEvent
}
