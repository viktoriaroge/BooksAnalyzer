package com.viroge.booksanalyzer.ui.screens.books.deleted

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.Book
import javax.inject.Inject

class RecentlyDeletedBookMapper @Inject constructor() {

    fun getScreenValues(): RecentlyDeletedScreenValues = RecentlyDeletedScreenValues(
        screenName = R.string.recently_deleted_screen_name,
        emptyStateTitle = R.string.recently_deleted_screen_empty_state_title,
        emptyStateSubtitle = R.string.recently_deleted_screen_empty_state_subtitle,
        sourceLabel = R.string.recently_deleted_screen_source_label,
        restoreDialogTitle = R.string.recently_deleted_screen_restore_dialog_title,
        restoreDialogText = R.string.recently_deleted_screen_restore_dialog_text,
        restoreButtonLabel = R.string.recently_deleted_screen_restore_dialog_restore_button_label,
        cancelButtonLabel = R.string.recently_deleted_screen_restore_dialog_cancel_button_label,
    )

    fun map(books: List<Book>): List<RecentlyDeletedBookState> {
        return books.map { book ->
            RecentlyDeletedBookState(
                id = book.id,
                title = book.title,
                authors = book.authors.joinToString(separator = ", "),
                metadata = listOfNotNull(
                    book.publishedYear?.toString(),
                    book.isbn13,
                ).joinToString(separator = " • "),
                coverUrl = book.coverUrl,
                coverHeaders = book.coverRequestHeaders,
                source = book.source,
            )
        }
    }
}
