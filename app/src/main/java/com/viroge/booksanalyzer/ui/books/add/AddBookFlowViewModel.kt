package com.viroge.booksanalyzer.ui.books.add

import androidx.lifecycle.ViewModel
import com.viroge.booksanalyzer.domain.BookCandidate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AddBookFlowViewModel @Inject constructor() : ViewModel() {

    private val _selectedCandidate = MutableStateFlow<BookCandidate?>(value = null)
    val selectedCandidate: StateFlow<BookCandidate?> = _selectedCandidate

    private val _prefillQuery = MutableStateFlow<String?>(value = null)
    val prefillQuery: StateFlow<String?> = _prefillQuery

    fun setCandidate(
        candidate: BookCandidate,
    ) {
        _selectedCandidate.value = candidate
        _prefillQuery.value = null
    }

    fun setManualPrefill(
        query: String,
    ) {
        _prefillQuery.value = query
        _selectedCandidate.value = null
    }

    fun clear() {
        _selectedCandidate.value = null
        _prefillQuery.value = null
    }
}
