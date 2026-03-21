package com.viroge.booksanalyzer.ui.common.action

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.repository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookActionViewModel @Inject constructor(
    private val booksRepo: BooksRepository,
) : ViewModel() {

    private val _events = Channel<BookActionEvent>(Channel.BUFFERED)
    val events: Flow<BookActionEvent> = _events.receiveAsFlow()

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
                        _events.send(BookActionEvent.BookDeleted(id = bookId, title = title))
                    }
                }
                .onFailure { _ -> _events.send(BookActionEvent.BookDeletingFailed(title = title)) }
        }
    }

    fun undoMarkToDelete() {
        val toRestoreBook = lastDeletedBook ?: return

        viewModelScope.launch {
            runCatching { booksRepo.restoreBookMarkedToDelete(bookId = toRestoreBook.id) }
                .onSuccess {
                    lastDeletedBook = null
                    _events.send(BookActionEvent.BookRestoreSuccess(title = toRestoreBook.title))
                }
                .onFailure { _ -> _events.send(BookActionEvent.BookRestoreFailed(title = toRestoreBook.title)) }
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
