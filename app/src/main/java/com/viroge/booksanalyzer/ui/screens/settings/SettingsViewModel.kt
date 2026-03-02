package com.viroge.booksanalyzer.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val mapper: SettingsMapper
) : ViewModel() {

    private val _itemTypesInOrder = MutableStateFlow(
        listOf(
            SettingsItemType.BOOKS_HEADER,
            SettingsItemType.RECENTLY_DELETED,
            SettingsItemType.GUIDE_HEADER,
            SettingsItemType.TERMS,
            SettingsItemType.APP_HEADER,
            SettingsItemType.VERSION,
        )
    )

    val state: StateFlow<SettingsUiState> = _itemTypesInOrder
        .map { types ->
            val version = BuildConfig.VERSION_NAME
            mapper.map(types, version)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState()
        )
}

enum class SettingsItemType {
    BOOKS_HEADER,
    RECENTLY_DELETED,
    GUIDE_HEADER,
    TERMS,
    APP_HEADER,
    VERSION,
}
