package com.viroge.booksanalyzer.ui.screens.books.scanner

import com.viroge.booksanalyzer.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanBookBarcodeMapper @Inject constructor() {

    fun getScreenValues(): ScannerScreenValues = ScannerScreenValues(
        screenName = R.string.scan_book_barcode_screen_name,
    )
}
