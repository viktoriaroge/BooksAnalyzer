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

    /**
     * Seed book is stripped down book that needs to later be updated from the DB.
     * Currently used by standard books that have their own ID in the DB.
     */
    private val _selectedBookSeed = MutableStateFlow<BookSeed?>(value = null)
    val selectedBookSeed: StateFlow<BookSeed?> = _selectedBookSeed

    fun getSelectedTempBook(): TempBook? = _selectedTempBook.value
    fun getSelectedBookSeed(): BookSeed? = _selectedBookSeed.value

    fun selectTempBook(
        book: TempBook,
    ) {
        _selectedTempBook.value = book
    }

    fun selectBookSeed(
        bookId: String,
        bookCoverUrl: String,
        bookCoverRequestHeaders: Map<String, String>,
        bookAnimationKey: String,
    ) {
        _selectedBookSeed.value = BookSeed(
            id = bookId,
            url = bookCoverUrl,
            headers = bookCoverRequestHeaders,
            animationKey = bookAnimationKey,
        )
    }

    fun clearTempSelection() {
        _selectedTempBook.value = null
    }

    fun clearSelection() {
        _selectedBookSeed.value = null
    }
}

data class BookSeed(
    val id: String,
    val url: String,
    val headers: Map<String, String>,
    val animationKey: String,
)
