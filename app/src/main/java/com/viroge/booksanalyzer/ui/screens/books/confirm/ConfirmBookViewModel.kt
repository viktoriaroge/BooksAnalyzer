package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.usecase.SaveBookUseCase
import com.viroge.booksanalyzer.domain.usecase.ValidateAndGetManualBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmBookViewModel @Inject constructor(
    private val saveBookUseCase: SaveBookUseCase,
    private val validateManualBook: ValidateAndGetManualBookUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ConfirmBookUiState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<ConfirmEvent>()
    val events = _events.asSharedFlow()

    fun saveBook(
        book: Book,
        selectedCoverUrl: String?,
    ) {
        if (_state.value.isSaving) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val finalBook = book.copy(coverUrl = selectedCoverUrl ?: book.coverUrl)

            saveBookUseCase(finalBook)
                .onSuccess { result ->
                    _events.emit(ConfirmEvent.Saved(result.bookId))
                }
                .onFailure { t ->
                    val msg = t.message ?: "Failed to save book"
                    _state.update { it.copy(error = msg) }
                    _events.emit(ConfirmEvent.Error(msg))
                }

            _state.update { it.copy(isSaving = false) }
        }
    }

    fun saveManualBook(
        title: String,
        authors: String,
        publishedYear: Int?,
        isbn13: String?,
        coverUrl: String?,
        selectedCoverUrl: String?,
    ) {
        validateManualBook(title, authors, publishedYear, isbn13, coverUrl)
            .onSuccess { book ->
                saveBook(book, selectedCoverUrl)
            }
            .onFailure { t ->
                _state.update { it.copy(error = t.message) }
            }
    }
}
