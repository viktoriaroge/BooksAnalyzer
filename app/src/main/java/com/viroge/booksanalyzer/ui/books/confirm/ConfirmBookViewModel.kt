package com.viroge.booksanalyzer.ui.books.confirm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.domain.Book
import com.viroge.booksanalyzer.domain.BookMapper.getManualBookEntry
import com.viroge.booksanalyzer.domain.BookSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmBookViewModel @Inject constructor(
    private val booksRepo: BooksRepository,
) : ViewModel() {

    private val _isSaving = MutableStateFlow(value = false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _error = MutableStateFlow<String?>(value = null)
    val error: StateFlow<String?> = _error

    private val _events = MutableSharedFlow<ConfirmEvent>()
    val events: SharedFlow<ConfirmEvent> = _events

    fun saveBook(book: Book) {
        if (_isSaving.value) return

        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null

            runCatching { booksRepo.insertFromBook(book) }
                .onSuccess { res ->
                    _events.emit(value = ConfirmEvent.Saved(res.bookId, res.wasInserted))
                }
                .onFailure { t ->
                    val msg = t.message ?: "Failed to save book"
                    _error.value = msg
                    _events.emit(value = ConfirmEvent.Error(message = msg))
                }

            _isSaving.value = false
        }
    }

    fun saveManualBook(
        title: String,
        authors: String,
        publishedYear: Int?,
        isbn13: String?,
        coverUrl: String?,
    ) {
        if (_isSaving.value) return
        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) {
            _error.value = "Title is required"
            return
        }

        saveBook(
            book = getManualBookEntry(
                title = title,
                authors = authors,
                publishedYear = publishedYear,
                isbn13 = isbn13,
                coverUrl = coverUrl,
            )
        )
    }
}

sealed interface ConfirmEvent {

    data class Saved(
        val bookId: String,
        val wasInserted: Boolean,
    ) : ConfirmEvent

    data class Error(
        val message: String,
    ) : ConfirmEvent
}
