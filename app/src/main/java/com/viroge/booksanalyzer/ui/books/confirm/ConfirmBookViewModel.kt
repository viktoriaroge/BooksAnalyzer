package com.viroge.booksanalyzer.ui.books.confirm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.BookSearchRepository
import com.viroge.booksanalyzer.domain.BookCandidate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmBookViewModel @Inject constructor(
    private val booksRepo: BookSearchRepository,
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun saveCandidate(candidate: BookCandidate, onSaved: (String) -> Unit) {
        if (_isSaving.value) return

        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null

            runCatching { booksRepo.insertFromCandidate(candidate) }
                .onSuccess { bookId -> onSaved(bookId) }
                .onFailure { t -> _error.value = t.message ?: "Failed to save book" }

            _isSaving.value = false
        }
    }
}
