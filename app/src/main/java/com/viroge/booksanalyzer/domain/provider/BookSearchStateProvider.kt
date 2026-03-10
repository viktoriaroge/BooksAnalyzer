package com.viroge.booksanalyzer.domain.provider

import com.viroge.booksanalyzer.domain.BooksUtil
import com.viroge.booksanalyzer.domain.model.library.SearchMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookSearchStateProvider @Inject constructor() {

    private val _prefillQuery = MutableStateFlow<String?>(value = null)
    val prefillQuery: StateFlow<String?> = _prefillQuery

    private val _prefillMode = MutableStateFlow<SearchMode?>(value = null)
    val prefillMode: StateFlow<SearchMode?> = _prefillMode

    fun getPrefillQuery(): String? = _prefillQuery.value
    fun getPrefillMode(): SearchMode? = _prefillMode.value

    fun setManualPrefill(
        query: String,
        mode: SearchMode,
    ) {
        val normalizedPrefillValue = when (mode) {
            SearchMode.ALL,
            SearchMode.TITLE,
            SearchMode.AUTHOR -> BooksUtil.normalizeForManualInput(string = query)

            SearchMode.ISBN -> query
        }

        _prefillQuery.value = normalizedPrefillValue
        _prefillMode.value = mode
    }

    fun clear() {
        _prefillQuery.value = null
        _prefillMode.value = null
    }
}