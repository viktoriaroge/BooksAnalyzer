package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.library.SearchMode
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
    private val saveBook: SaveBookUseCase,
    private val validateManualBook: ValidateAndGetManualBookUseCase,
    private val mapper: ConfirmBookMapper,
) : ViewModel() {

    private val _state = MutableStateFlow(ConfirmBookUiState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<ConfirmEvent>()
    val events = _events.asSharedFlow()

    fun initializeWithBook(
        book: Book?,
        prefillQuery: String?,
        prefillMode: SearchMode?,
    ) {
        _state.update { newState ->
            val manualData = mapper.mapToManualFormData(prefillQuery, prefillMode)
            newState.copy(
                screenValues = mapper.getScreenValues(),
                bookData = book?.let { mapper.mapToDataState(it) },
                manualFormData = manualData,
                titleInput = newState.titleInput.ifBlank { manualData.title },
                authorsInput = newState.authorsInput.ifBlank { manualData.authors },
                isbn13Input = newState.isbn13Input.ifBlank { manualData.isbn13 },
            )
        }
    }

    fun updateCover(
        selectedCoverUrl: String?,
        selectedCoverHeaders: Map<String, String>,
    ) {
        _state.update { newState ->
            newState.copy(
                selectedCoverUrl = selectedCoverUrl,
                selectedCoverHeaders = selectedCoverHeaders,
            )
        }
    }

    fun saveBook(
        book: Book,
        selectedCoverUrl: String?,
        selectedCoverHeaders: Map<String, String>?,
    ) {
        if (_state.value.isSaving) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val finalBook = book.copy(
                coverUrl = selectedCoverUrl ?: book.coverUrl,
                coverRequestHeaders = selectedCoverHeaders ?: book.coverRequestHeaders,
            )

            saveBook(finalBook)
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
        selectedCoverUrl: String?,
        selectedCoverHeaders: Map<String, String>?,
    ) {
        val s = _state.value
        validateManualBook(
            title = s.titleInput,
            authors = s.authorsInput,
            year = s.yearInput,
            isbn13 = s.isbn13Input,
        ).onSuccess { book ->
            saveBook(book, selectedCoverUrl, selectedCoverHeaders)
        }.onFailure { t ->
            _state.update { it.copy(error = t.message) }
        }
    }

    fun onTitleChange(value: String) = _state.update { it.copy(titleInput = value) }
    fun onAuthorsChange(value: String) = _state.update { it.copy(authorsInput = value) }
    fun onYearChange(value: String) = _state.update { it.copy(yearInput = value) }
    fun onIsbnChange(value: String) = _state.update { it.copy(isbn13Input = value) }

    fun getTemporaryBookForCoverPicker(
        source: BookSource,
        coverUrl: String?,
    ): Book {
        val s = _state.value
        return mapper.mapToTempBookForCoverPicker(
            s.titleInput,
            s.authorsInput,
            s.yearInput,
            s.isbn13Input,
            source,
            coverUrl,
        )
    }
}
