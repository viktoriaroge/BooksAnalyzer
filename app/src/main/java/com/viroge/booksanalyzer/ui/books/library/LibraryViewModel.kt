package com.viroge.booksanalyzer.ui.books.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.data.local.BookEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    booksRepo: BooksRepository,
) : ViewModel() {

    val books: StateFlow<List<BookEntity>> =
        booksRepo.observeLibrary()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )
}

