package com.viroge.booksanalyzer.ui.screens.books.cover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.provider.CoverPickerStateProvider
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.usecase.GetBookCoverCandidatesUseCase
import com.viroge.booksanalyzer.domain.usecase.GetBookCoverHeadersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoverPickerViewModel @Inject constructor(
    private val pickerStateProvider: CoverPickerStateProvider,
    private val getCoverCandidates: GetBookCoverCandidatesUseCase,
    private val getCoverHeaders: GetBookCoverHeadersUseCase,
    private val mapper: BookCoverMapper,
) : ViewModel() {

    private val _state = MutableStateFlow(BookCoverPickerScreenState())
    val state = combine(_state, pickerStateProvider.state) { innerState, pickerState ->
        BookCoverPickerUiState(
            screenState = innerState,
            coverState = pickerState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = BookCoverPickerUiState()
    )

    fun openCoverPicker(
        originalCoverUrl: String?,
        originalCoverRequestHeaders: Map<String, String>,
        source: BookSource,
        isbn13: String?,
    ) {
        _state.update {
            it.copy(
                initialized = true,
                isOpen = true,
                isLoading = true,
                screenValues = mapper.getStaticScreenValues(),
            )
        }

        viewModelScope.launch {
            val allBookCovers = getCoverCandidates(originalCoverUrl, source, isbn13)
            pickerStateProvider.updateBookCovers(bookCovers = allBookCovers)
        }

        val selected = pickerStateProvider.getSelected()
        if (selected == null && originalCoverUrl != null) {
            pickerStateProvider.selectCover(
                url = originalCoverUrl,
                headers = originalCoverRequestHeaders,
            )
        }

        _state.update { it.copy(isLoading = false) }
    }

    fun closeCoverPicker() {
        _state.update { it.copy(isOpen = false) }
    }

    fun onManualUrlChange(newUrl: String) {
        _state.update { it.copy(manualUrlInput = newUrl) }
    }

    fun addManualUrl() {
        val url = _state.value.manualUrlInput.trim()
        if (url.isEmpty()) return

        val bookCovers = pickerStateProvider.getBookCoverCandidates()
        val manualBookCovers = pickerStateProvider.getManualBookCoverCandidates()

        if (bookCovers.any { it.url == url } || manualBookCovers.any { it.url == url }) {
            _state.update { it.copy(manualUrlInput = "") }
            return
        }

        _state.update { it.copy(manualUrlInput = "") }

        pickerStateProvider.selectCover(
            url = url,
            headers = getCoverHeaders(url),
            isManualInput = true,
        )
    }

    fun selectCover(url: String) {
        pickerStateProvider.selectCover(
            url = url,
            headers = getCoverHeaders(url),
        )
        closeCoverPicker()
    }
}
