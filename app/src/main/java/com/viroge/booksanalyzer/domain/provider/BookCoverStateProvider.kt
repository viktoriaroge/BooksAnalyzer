package com.viroge.booksanalyzer.domain.provider

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoverPickerStateProvider @Inject constructor() {

    private val _selectedCoverUrl: MutableStateFlow<String?> = MutableStateFlow(null)
    val selectedCoverUrl = _selectedCoverUrl.asStateFlow()

    fun getSelectedCoverUrl(): String? = _selectedCoverUrl.value

    fun selectCoverUrl(
        url: String?,
    ) {
        _selectedCoverUrl.value = url
    }

    fun clear() {
        _selectedCoverUrl.value = null
    }
}
