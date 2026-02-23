package com.viroge.booksanalyzer.ui.screens.books.add

import androidx.lifecycle.ViewModel
import com.viroge.booksanalyzer.domain.Book
import com.viroge.booksanalyzer.domain.BooksUtil.normalizeForManualInput
import com.viroge.booksanalyzer.domain.SearchMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AddBookFlowViewModel @Inject constructor() : ViewModel() {

    private val _selectedBook = MutableStateFlow<Book?>(value = null)
    val selectedBook: StateFlow<Book?> = _selectedBook

    private val _prefillQuery = MutableStateFlow<String?>(value = null)
    val prefillQuery: StateFlow<String?> = _prefillQuery

    private val _prefillMode = MutableStateFlow<SearchMode?>(value = null)
    val prefillMode: StateFlow<SearchMode?> = _prefillMode

    fun setBook(
        book: Book,
    ) {
        _selectedBook.value = book
        _prefillQuery.value = null
        _prefillMode.value = null
    }

    fun setManualPrefill(
        query: String,
        mode: SearchMode,
    ) {
        val normalizedPrefillValue = when (mode) {
            SearchMode.ALL,
            SearchMode.TITLE,
            SearchMode.AUTHOR -> normalizeForManualInput(string = query)

            SearchMode.ISBN -> query
        }

        _prefillQuery.value = normalizedPrefillValue
        _prefillMode.value = mode
        _selectedBook.value = null
    }

    fun clear() {
        _selectedBook.value = null
        _prefillQuery.value = null
        _prefillMode.value = null
    }
}
