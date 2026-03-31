package com.viroge.booksanalyzer.ui.nav

import com.viroge.booksanalyzer.ui.nav.StateArguments.BOOK_SEED_ARG
import com.viroge.booksanalyzer.ui.nav.StateArguments.TRANSITION_PREFIX_ARG

object Routes {

    // --- Top-Level screens -------------------------------------------------

    // NOTE: Always keep Top-Level screen routes unparameterized!
    const val LIBRARY_GRAPH = "library_graph"
    const val LIBRARY_ROUTE = "library"

    const val SEARCH_BOOK_GRAPH = "search_book_graph"
    const val SEARCH_BOOK_ROUTE = "search_book"

    const val SETTINGS_GRAPH = "settings_graph"
    const val SETTINGS_ROUTE = "settings"

    // --- Stand-Alone screens ------------------------------------------------

    const val COLLECTION_ROUTE_PREFIX = "collection"
    const val COLLECTION_ROUTE = "$COLLECTION_ROUTE_PREFIX/{${TRANSITION_PREFIX_ARG}}"

    const val CONFIRM_BOOK_ROUTE_PREFIX = "confirm_book"
    const val CONFIRM_BOOK_ROUTE = "${CONFIRM_BOOK_ROUTE_PREFIX}/{${BOOK_SEED_ARG}}/{${TRANSITION_PREFIX_ARG}}"

    const val BOOK_DETAILS_ROUTE_PREFIX = "book_details"
    const val BOOK_DETAILS_ROUTE = "${BOOK_DETAILS_ROUTE_PREFIX}/{${BOOK_SEED_ARG}}/{${TRANSITION_PREFIX_ARG}}"

    const val SCAN_BOOK_BARCODE_ROUTE = "scan_book_barcode_route"

    const val RECENTLY_DELETED_BOOKS_ROUTE = "recently_deleted_books"

    const val APP_TERMS_ROUTE = "app_terms"
}
