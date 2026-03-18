package com.viroge.booksanalyzer.ui

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.repository.BooksRepository
import com.viroge.booksanalyzer.data.sync.book.DeleteBooksScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainSharedViewModel @Inject constructor(
    private val booksRepo: BooksRepository,
    private val deleteBooksScheduler: DeleteBooksScheduler,
) : ViewModel() {

    private val _events = MutableSharedFlow<AppEvent>()
    val events: SharedFlow<AppEvent> = _events

    private var lastDeletedBook: MarkedBook? = null

    private val _isSplashTimerFinished = MutableStateFlow(false)
    private var _isDoneCleaningUp = MutableStateFlow(value = false)
    val isLoading = combine(_isDoneCleaningUp, _isSplashTimerFinished) { ready, timedOut -> !(ready && timedOut) }
        .stateIn(
            scope = viewModelScope,
            // Eagerly ensures the 'combine' logic runs even before the UI starts listening
            started = SharingStarted.Eagerly,
            initialValue = true,
        )

    init {
        // As soon as the app launches the main activity, attempt to do DB maintenance:
        viewModelScope.launch {
            Log.d("MainSharedViewModel", "deleteStaleMarkedBooks called")
            runCatching {
                deleteBooksScheduler.enqueueBulkDelete()
                _isDoneCleaningUp.value = true
            }.onFailure { e ->
                Log.d("MainSharedViewModel", "deleteStaleMarkedBooks failed with exception: $e")
                _isDoneCleaningUp.value = true
            }
        }
        // Make sure the splash stays onscreen for at least 1 whole second:
        viewModelScope.launch {
            delay(1500)
            _isSplashTimerFinished.value = true
        }
    }

    fun markToDelete(bookId: String, title: String) {
        viewModelScope.launch {
            runCatching { booksRepo.markBookToDelete(bookId) }
                .onSuccess { deletedBookData ->
                    lastDeletedBook = MarkedBook(
                        id = bookId,
                        title = title,
                    )

                    if (deletedBookData != null) {
                        _events.emit(AppEvent.BookDeleted(id = bookId, title = title))
                    }
                }
                .onFailure { _ -> _events.emit(AppEvent.BookDeletingFailed(title = title)) }
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

@Immutable
data class MarkedBook(
    val id: String,
    val title: String,
)

sealed interface AppEvent {
    @Immutable
    data class BookDeleted(
        val id: String,
        val title: String,
    ) : AppEvent

    @Immutable
    data class BookDeletingFailed(
        val title: String,
    ) : AppEvent

    @Immutable
    data class BookRestoreSuccess(
        val title: String,
    ) : AppEvent

    @Immutable
    data class BookRestoreFailed(
        val title: String,
    ) : AppEvent
}
