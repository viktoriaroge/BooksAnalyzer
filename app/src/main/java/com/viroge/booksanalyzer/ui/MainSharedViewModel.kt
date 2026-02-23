package com.viroge.booksanalyzer.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.domain.Book
import com.viroge.booksanalyzer.domain.delete.DeleteBooksScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainSharedViewModel @Inject constructor(
    private val booksRepo: BooksRepository,
    private val deleteBooksScheduler: DeleteBooksScheduler,
) : ViewModel() {

    private val _events = MutableSharedFlow<AppEvent>()
    val events: SharedFlow<AppEvent> = _events

    private var lastDeletedBook: Book? = null

    init {
        deleteStaleMarkedBooks()
    }

    fun deleteStaleMarkedBooks() {
        // As soon as the app launches the main activity, attempt to do DB maintenance:
        viewModelScope.launch {
            Log.d("MainSharedViewModel", "deleteStaleMarkedBooks called")
            runCatching { deleteBooksScheduler.enqueueBulkDelete() }
                .onFailure { e -> Log.d("MainSharedViewModel", "deleteStaleMarkedBooks failed with exception: $e") }
        }
    }

    fun markToDelete(book: Book) {
        viewModelScope.launch {
            runCatching { booksRepo.markBookToDelete(book.id) }
                .onSuccess { deletedBookData ->
                    lastDeletedBook = book

                    if (deletedBookData != null) {
                        _events.emit(AppEvent.BookDeleted(id = book.id, title = book.title))
                    }
                }
                .onFailure { _ -> _events.emit(AppEvent.BookDeletingFailed(title = book.title)) }
        }
    }

    fun undoMarkToDelete() {
        val toRestoreBook = lastDeletedBook ?: return

        viewModelScope.launch {
            runCatching { booksRepo.restoreBookMarkedToDelete(bookId = toRestoreBook.id) }
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
