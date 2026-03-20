package com.viroge.booksanalyzer.ui.screens.terms

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TermsViewModel @Inject constructor(
    private val mapper: TermsMapper,
) : ViewModel() {

    private val _itemTypesInOrder = MutableStateFlow(
        listOf(
            TermsItemType.PAGEVOW,
            TermsItemType.BOOK_COVER,
            TermsItemType.LIBRARY,
            TermsItemType.DELETE_BOOK,
            TermsItemType.RECENTLY_DELETED_BOOKS,
            TermsItemType.SETTINGS,
            TermsItemType.SOURCE,
        )
    )

    val state: StateFlow<TermsUiState> = _itemTypesInOrder
        .map { types ->
            mapper.map(types)
        }
        .distinctUntilChanged()
        .flowOn(Dispatchers.Default)
        .catch { _ -> Log.e("TermsViewModel", "Failed to prepare ui state.") } // TODO: Send error to UI
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TermsUiState()
        )
}

enum class TermsItemType {
    PAGEVOW,
    BOOK_COVER,
    LIBRARY,
    DELETE_BOOK,
    RECENTLY_DELETED_BOOKS,
    SETTINGS,
    SOURCE,
}
