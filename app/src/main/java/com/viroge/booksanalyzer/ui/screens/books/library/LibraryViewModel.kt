package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.provider.BookSelectionStateProvider
import com.viroge.booksanalyzer.domain.usecase.book.GetBookUseCase
import com.viroge.booksanalyzer.domain.usecase.book.LibrarySort
import com.viroge.booksanalyzer.domain.usecase.book.ObserveLibraryDataUseCase
import com.viroge.booksanalyzer.ui.screens.books.BookTransitionKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val bookSelectionStateProvider: BookSelectionStateProvider,
    observeLibraryDataUseCase: ObserveLibraryDataUseCase,
    private val getBookUseCase: GetBookUseCase,
    private val mapper: LibraryMapper,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val screenState: Flow<LibraryScreenState> =
        observeLibraryDataUseCase("", null, LibrarySort.RECENT)
            .map { data ->
                when {
                    data.books.isEmpty() -> LibraryScreenState.Empty(
                        navRoute = LibraryNavDirection.SEARCH,
                        emptyStateValues = mapper.getEmptyStateValues(),
                    )

                    data.currentlyReading.isEmpty() -> LibraryScreenState.Empty(
                        navRoute = LibraryNavDirection.COLLECTION,
                        emptyStateValues = mapper.getEmptyStateNoCurrentsValues(),
                    )

                    else -> LibraryScreenState.Content(
                        contentStateValues = mapper.getContentStateValues(),
                        currentBooks = data.currentlyReading.map { mapper.mapToData(it) },
                    )
                }
            }.flowOn(Dispatchers.Default)

    val state: StateFlow<LibraryUiState> = screenState
        .map { state ->
            LibraryUiState(
                screenValues = mapper.getScreenValues(),
                screenState = state,
            )
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = LibraryUiState(
                screenValues = LibraryScreenValues(),
                screenState = LibraryScreenState.Loading,
            )
        )

    fun selectBook(bookId: String) {
        viewModelScope.launch {
            getBookUseCase(bookId)?.let { book ->
                bookSelectionStateProvider.selectBookSeed(
                    bookId = book.id,
                    bookCoverUrl = book.coverUrl ?: "",
                    bookCoverRequestHeaders = book.coverRequestHeaders,
                    bookAnimationKey = BookTransitionKey.calculate(
                        title = book.title,
                        authors = book.authors,
                        isbn = book.isbn13,
                        source = book.source,
                        sourceId = book.sourceId,
                    )
                )
            }
        }
    }
}
