package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.Book
import com.viroge.booksanalyzer.domain.SearchMode
import com.viroge.booksanalyzer.ui.components.BookSourceBadge
import com.viroge.booksanalyzer.ui.components.CommonAsyncImage
import com.viroge.booksanalyzer.ui.components.CommonAsyncImageSize
import com.viroge.booksanalyzer.ui.components.CommonLinearProgressIndicator
import com.viroge.booksanalyzer.ui.components.CommonTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBookScreen(
    book: Book?,
    headersForBookCover: Map<String, String>,
    selectedCoverUrl: String?,
    prefillQuery: String?,
    prefillMode: SearchMode?,
    isSaving: Boolean,
    error: String?,
    onOpenCoverPicker: () -> Unit,
    onBack: () -> Unit,
    onConfirmSave: () -> Unit,
    onConfirmSaveManual: (
        title: String,
        authors: String,
        publishedYear: Int?,
        isbn13: String?,
        coverUrl: String?,
    ) -> Unit = { _, _, _, _, _ -> },
) {

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CommonTopAppBar(
                title =
                    if (book != null) stringResource(R.string.confirm_book_screen_name)
                    else stringResource(R.string.confirm_book_screen_in_manual_mode_name),
                canGoBack = true,
                onBack = onBack,
            )
        }
    ) { screenPadding ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding()) // top bar
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {

            if (isSaving) {
                Spacer(Modifier.height(height = 12.dp))

                CommonLinearProgressIndicator()
            }

            error?.let { msg ->
                Spacer(Modifier.height(height = 12.dp))
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(Modifier.height(height = 16.dp))

            when {
                book != null -> {
                    val coverToShow = selectedCoverUrl ?: book.coverUrl

                    CommonAsyncImage(
                        modifier = Modifier.fillMaxWidth(),
                        url = coverToShow,
                        requestHeaders = headersForBookCover,
                        size = CommonAsyncImageSize.LARGE,
                    )

                    Spacer(Modifier.height(height = 8.dp))

                    Button(
                        onClick = onOpenCoverPicker,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(R.string.confirm_book_screen_change_book_cover_button_label))
                    }

                    Spacer(Modifier.height(height = 24.dp))

                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                    )

                    if (book.authors.isNotEmpty()) {
                        Text(
                            text = book.authors.joinToString(separator = ", "),
                        )
                    }

                    book.isbn13?.let {
                        Text(text = stringResource(R.string.confirm_book_screen_isbn13_label, it))
                    }

                    Spacer(modifier = Modifier.height(height = 8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(space = 2.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(R.string.confirm_book_screen_source_label),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        BookSourceBadge(
                            source = book.source,
                            modifier = Modifier.padding(all = 2.dp),
                            showFullSourceName = true,
                        )
                        Spacer(modifier = Modifier.weight(weight = 1f))
                    }

                    Spacer(Modifier.height(height = 8.dp))

                    Button(
                        onClick = onConfirmSave,
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(text = stringResource(R.string.confirm_book_screen_save_button_label)) }

                    Spacer(Modifier.height(height = 16.dp))
                }

                !prefillQuery.isNullOrBlank() -> {
                    ManualBookForm(
                        prefillQuery = prefillQuery,
                        prefillMode = prefillMode ?: SearchMode.ALL,
                        isSaving = isSaving,
                        onSave = onConfirmSaveManual,
                    )
                }

                else -> { /* noop */
                }
            }
        }
    }
}
