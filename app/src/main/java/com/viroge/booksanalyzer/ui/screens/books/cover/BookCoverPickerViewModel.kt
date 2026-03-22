package com.viroge.booksanalyzer.ui.screens.books.cover

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.provider.CoverPickerStateProvider
import com.viroge.booksanalyzer.domain.usecase.bookcover.GetBookCoverUrlsUseCase
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
    private val getBookCoverUrlsUseCase: GetBookCoverUrlsUseCase,
    private val mapper: BookCoverMapper,
) : ViewModel() {

    private val _isInitialized = MutableStateFlow(false)
    private val _isOpen = MutableStateFlow(false)
    private val _manualInput = MutableStateFlow("")
    private val _manualBookCovers: MutableStateFlow<List<BookCover>> = MutableStateFlow(emptyList())
    private val _bookCovers: MutableStateFlow<List<BookCover>> = MutableStateFlow(emptyList())
    private val _state = MutableStateFlow(InnerState.Loading)

    private enum class InnerState {
        Loading, Content
    }

    val state = combine(
        _isInitialized,
        _isOpen,
        _state,
        _manualInput,
        _manualBookCovers,
        _bookCovers,
        pickerStateProvider.selectedCoverUrl
    ) { args ->
        val isInitialized = args[0] as Boolean
        val isOpen = args[1] as Boolean
        val innerState = args[2] as InnerState
        val input = args[3] as String
        val manualBookCovers = args[4] as List<BookCover>
        val bookCovers = args[5] as List<BookCover>
        val selected = args[6] as String?

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
                    selectedCover = selected?.let { mapper.map(it) },
                    bookCovers = // Show manual first, then the rest:
                        manualBookCovers.map { mapper.map(it) } +
                                bookCovers.map { mapper.map(it) },
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

    init {
        _isInitialized.value = false
    }

    fun openCoverPicker(
        selectedCoverUrl: String?,
        originalCoverUrl: String?,
        isbn13: String?,
    ) {
        _isOpen.value = true

        val isInitialized = _isInitialized.value
        if (!isInitialized) {
            _state.value = InnerState.Loading

            val allBookCovers = getBookCoverUrlsUseCase(selectedCoverUrl, originalCoverUrl, isbn13)
            _bookCovers.value = allBookCovers.map { BookCover(url = it) }

            val selected = pickerStateProvider.getSelectedCoverUrl()
            if (selected == null && selectedCoverUrl != null) {
                pickerStateProvider.selectCoverUrl(url = selectedCoverUrl)
            }
            _state.value = InnerState.Content

            _isInitialized.value = true
        }
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

        val bookCovers = _bookCovers.value
        val manualBookCovers = _manualBookCovers.value

        if (bookCovers.any { it.url == url } || manualBookCovers.any { it.url == url }) {
            _manualInput.value = ""
            return
        }

        _manualInput.value = ""
        _manualBookCovers.value = listOf(BookCover(url = url)) + manualBookCovers

        pickerStateProvider.selectCoverUrl(url = url)
    }

    fun selectCover(url: String) {
        pickerStateProvider.selectCoverUrl(url = url)
        closeCoverPicker()
    }

    fun removeInvalidUrl(url: String) {
        Log.d("CoverPickerViewModel", "Removing invalid url: $url")

        val bookCovers = _bookCovers.value
        if (bookCovers.any { it.url == url }) _bookCovers.value = bookCovers.filterNot { it.url == url }

        val manualBookCovers = _manualBookCovers.value
        if (manualBookCovers.any { it.url == url }) _manualBookCovers.value = manualBookCovers.filterNot { it.url == url }
    }
}

data class BookCover(
    val url: String,
)
