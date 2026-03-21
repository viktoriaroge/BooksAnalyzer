package com.viroge.booksanalyzer.ui.common.action

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.repository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainSharedViewModel @Inject constructor(
    private val booksRepo: BooksRepository,
) : ViewModel() {

    private val _events = MutableSharedFlow<BookActionEvent>()
    val events: SharedFlow<BookActionEvent> = _events

    private var lastDeletedBook: MarkedBook? = null

    fun markToDelete(bookId: String, title: String) {
        viewModelScope.launch {
            runCatching { booksRepo.markBookToDelete(bookId) }
                .onSuccess { deletedBookData ->
                    lastDeletedBook = MarkedBook(
                        id = bookId,
                        title = title,
                    )

                    if (deletedBookData != null) {
                        _events.emit(BookActionEvent.BookDeleted(id = bookId, title = title))
                    }
                }
                .onFailure { _ -> _events.emit(BookActionEvent.BookDeletingFailed(title = title)) }
        }
    }

    fun undoMarkToDelete() {
        val toRestoreBook = lastDeletedBook ?: return

        viewModelScope.launch {
            runCatching { booksRepo.restoreBookMarkedToDelete(bookId = toRestoreBook.id) }
                .onSuccess {
                    lastDeletedBook = null
                    _events.emit(BookActionEvent.BookRestoreSuccess(title = toRestoreBook.title))
                }
                .onFailure { _ -> _events.emit(BookActionEvent.BookRestoreFailed(title = toRestoreBook.title)) }
        }
    }
}

@Immutable
data class MarkedBook(
    val id: String,
    val title: String,
)

sealed interface BookActionEvent {
    @Immutable
    data class BookDeleted(
        val id: String,
        val title: String,
    ) : BookActionEvent

    @Immutable
    data class BookDeletingFailed(
        val title: String,
    ) : BookActionEvent

    @Immutable
    data class BookRestoreSuccess(
        val title: String,
    ) : BookActionEvent

    @Immutable
    data class BookRestoreFailed(
        val title: String,
    ) : BookActionEvent
}
