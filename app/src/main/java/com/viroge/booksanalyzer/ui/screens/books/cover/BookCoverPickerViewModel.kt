package com.viroge.booksanalyzer.ui.screens.books.cover

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.provider.CoverPickerStateProvider
import com.viroge.booksanalyzer.domain.usecase.bookcover.GetBookCoverCandidatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CoverPickerViewModel @Inject constructor(
    private val pickerStateProvider: CoverPickerStateProvider,
    private val getCoverCandidates: GetBookCoverCandidatesUseCase,
    private val mapper: BookCoverMapper,
) : ViewModel() {

    private val _isInitialized = MutableStateFlow(false)
    private val _isOpen = MutableStateFlow(false)
    private val _manualInput = MutableStateFlow("")
    private val _state = MutableStateFlow(InnerState.Loading)

    private enum class InnerState {
        Loading, Content
    }

    val state = combine(
        _isInitialized,
        _isOpen,
        _manualInput,
        _state,
        pickerStateProvider.state
    ) { isInitialized, isOpen, input, innerState, pickerState ->
        when (innerState) {
            InnerState.Loading -> BookCoverPickerUiState(
                initialized = isInitialized,
                isOpen = isOpen,
                screenValues = mapper.getStaticScreenValues(),
                screenState = BookCoverPickerScreenState.Loading,
            )

            InnerState.Content -> BookCoverPickerUiState(
                initialized = isInitialized,
                isOpen = isOpen,
                screenValues = mapper.getStaticScreenValues(),
                screenState = BookCoverPickerScreenState.Content(
                    manualUrlInput = input,
                    values = mapper.getContentStateValues(),
                    selectedCover = pickerState.selectedCandidate?.let { mapper.map(it) },
                    bookCovers = // Show manual first, then the rest:
                        pickerState.manualBookCovers.map { mapper.map(it) } +
                                pickerState.bookCovers.map { mapper.map(it) },
                ),
            )
        }
    }.distinctUntilChanged()
        .flowOn(Dispatchers.Default)
        .catch { _ -> Log.e("CoverPickerViewModel", "Failed to prepare ui state.") } // TODO: Send error to UI
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = BookCoverPickerUiState(
                initialized = _isInitialized.value,
                isOpen = _isOpen.value,
                screenValues = mapper.getStaticScreenValues(),
                screenState = BookCoverPickerScreenState.Loading,
            )
        )

    fun openCoverPicker(
        selectedCoverUrl: String?,
        originalCoverUrl: String?,
        isbn13: String?,
    ) {
        _isInitialized.value = true
        _isOpen.value = true
        _state.value = InnerState.Loading

        val allBookCovers = getCoverCandidates(selectedCoverUrl, originalCoverUrl, isbn13)
        pickerStateProvider.updateBookCovers(bookCovers = allBookCovers)

        val selected = pickerStateProvider.getSelected()
        if (selected == null && selectedCoverUrl != null) {
            pickerStateProvider.selectCover(url = selectedCoverUrl)
        }

        _state.value = InnerState.Content
    }

    fun closeCoverPicker() {
        _isOpen.value = false
    }

    fun onManualUrlChange(newUrl: String) {
        _manualInput.value = newUrl
    }

    fun addManualUrl() {
        val url = _manualInput.value.trim()
        if (url.isEmpty()) return

        val bookCovers = pickerStateProvider.getBookCoverCandidates()
        val manualBookCovers = pickerStateProvider.getManualBookCoverCandidates()

        if (bookCovers.any { it.url == url } || manualBookCovers.any { it.url == url }) {
            _manualInput.value = ""
            return
        }

        _manualInput.value = ""

        pickerStateProvider.selectCover(
            url = url,
            isManualInput = true,
        )
    }

    fun selectCover(url: String) {
        pickerStateProvider.selectCover(
            url = url,
        )
        closeCoverPicker()
    }
}
