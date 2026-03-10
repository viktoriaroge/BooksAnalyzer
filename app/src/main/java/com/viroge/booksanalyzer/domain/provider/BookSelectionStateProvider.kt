package com.viroge.booksanalyzer.domain.provider

import com.viroge.booksanalyzer.domain.model.TempBook
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookSelectionStateProvider @Inject constructor() {

    /**
     * Temp book is a means to save a temporary book copy
     * when the copy is not kept in the DB and cannot be obtained from there.
     * NOTE: This is an unreliable source, currently used for search results.
     */
    private val _selectedTempBook = MutableStateFlow<TempBook?>(value = null)
    val selectedTempBook: StateFlow<TempBook?> = _selectedTempBook

    private val _selectedBookId = MutableStateFlow<String?>(value = null)
    val selectedBookId: StateFlow<String?> = _selectedBookId

    fun getSelectedTempBook(): TempBook? = _selectedTempBook.value
    fun getSelectedBookId(): String? = _selectedBookId.value

    fun selectTempBook(
        book: TempBook,
    ) {
        _selectedTempBook.value = book
    }

    fun selectBookId(
        bookId: String,
    ) {
        _selectedBookId.value = bookId
    }

    fun clearTempSelection() {
        _selectedTempBook.value = null
    }

    fun clearSelection() {
        _selectedBookId.value = null
    }
}
