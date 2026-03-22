package com.viroge.booksanalyzer.ui.screens.books.deleted

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentlyDeletedBookMapper @Inject constructor() {

    fun getScreenValues(): RecentlyDeletedScreenValues = RecentlyDeletedScreenValues(
        screenName = R.string.recently_deleted_screen_name,
    )

    fun getEmptyStateValues(): RecentlyDeletedEmptyValues = RecentlyDeletedEmptyValues(
        emptyStateTitle = R.string.recently_deleted_screen_empty_state_title,
        emptyStateText = R.string.recently_deleted_screen_empty_state_subtitle,
    )

    fun getContentStateValues(): RecentlyDeletedContentValues = RecentlyDeletedContentValues(
        sourceLabel = R.string.recently_deleted_screen_source_label,
        restoreDialogTitle = R.string.recently_deleted_screen_restore_dialog_title,
        restoreDialogText = R.string.recently_deleted_screen_restore_dialog_text,
        restoreButtonLabel = R.string.recently_deleted_screen_restore_dialog_restore_button_label,
        cancelButtonLabel = R.string.recently_deleted_screen_restore_dialog_cancel_button_label,
    )

    fun map(books: List<Book>): List<RecentlyDeletedBookState> = books
        .map { book ->
            RecentlyDeletedBookState(
                id = book.id,
                title = book.title,
                authors = book.authors.joinToString(separator = ", "),
                metadata = listOfNotNull(book.publishedYear, book.isbn13).joinToString(separator = " • "),
                coverUrl = book.coverUrl,
                source = BookSourceUi.fromDomain(book.source),
            )
        }
}
