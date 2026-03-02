package com.viroge.booksanalyzer.ui.screens.terms

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.viroge.booksanalyzer.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TermsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(value = TermsUiState())
    val state: StateFlow<TermsUiState> = _state.asStateFlow()

    init {
        _state.value = TermsUiState(
            settingsEntries = listOf(
                // --- Terms -------------------
                TermsEntry(
                    icon = Icons.Default.Person,
                    showTitle = true,
                    titleRes = R.string.terms_screen_pagevow_title,
                    showSubtitle = true,
                    subtitleRes = R.string.terms_screen_pagevow_desc,
                ),
                TermsEntry(
                    icon = Icons.Default.Person,
                    showTitle = true,
                    titleRes = R.string.terms_screen_visage_title,
                    showSubtitle = true,
                    subtitleRes = R.string.terms_screen_visage_desc,
                ),
                TermsEntry(
                    icon = Icons.Default.Person,
                    showTitle = true,
                    titleRes = R.string.terms_screen_chronicle_title,
                    showSubtitle = true,
                    subtitleRes = R.string.terms_screen_chronicle_desc,
                ),
                TermsEntry(
                    icon = Icons.Default.Person,
                    showTitle = true,
                    titleRes = R.string.terms_screen_banish_title,
                    showSubtitle = true,
                    subtitleRes = R.string.terms_screen_banish_desc,
                ),
                TermsEntry(
                    icon = Icons.Default.Person,
                    showTitle = true,
                    titleRes = R.string.terms_screen_exile_title,
                    showSubtitle = true,
                    subtitleRes = R.string.terms_screen_exile_desc,
                ),
                TermsEntry(
                    icon = Icons.Default.Person,
                    showTitle = true,
                    titleRes = R.string.terms_screen_scriptorium_title,
                    showSubtitle = true,
                    subtitleRes = R.string.terms_screen_scriptorium_desc,
                ),
                TermsEntry(
                    icon = Icons.Default.Person,
                    showTitle = true,
                    titleRes = R.string.terms_screen_origin_title,
                    showSubtitle = true,
                    subtitleRes = R.string.terms_screen_origin_desc,
                ),
            ),
        )
    }
}

data class TermsUiState(
    val settingsEntries: List<TermsEntry> = emptyList(),
)

data class TermsEntry(
    val icon: ImageVector? = null,
    val showTitle: Boolean = false,
    val title: String? = null,
    @param:StringRes val titleRes: Int = R.string.empty_text,
    val showSubtitle: Boolean = false,
    val subtitle: String? = null,
    @param:StringRes val subtitleRes: Int = R.string.empty_text,
)

