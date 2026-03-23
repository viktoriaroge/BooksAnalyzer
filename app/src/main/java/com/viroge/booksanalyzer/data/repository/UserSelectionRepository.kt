package com.viroge.booksanalyzer.data.repository

import com.viroge.booksanalyzer.domain.model.BookCoverDataSeed
import com.viroge.booksanalyzer.domain.model.BookSeed
import com.viroge.booksanalyzer.domain.model.TempBook
import kotlinx.coroutines.flow.Flow

interface UserSelectionRepository {

    val selectedTempBook: Flow<TempBook?>
    val selectedBookSeed: Flow<BookSeed?>
    val selectedCoverUrl: Flow<String?>
    val selectedBookCoverDataSeed: Flow<BookCoverDataSeed?>

    suspend fun updateTempBook(book: TempBook?)
    suspend fun updateBookSeed(seed: BookSeed?)
    suspend fun updateCoverUrl(url: String?)
    suspend fun updateBookCoverSeed(seed: BookCoverDataSeed?)
}
