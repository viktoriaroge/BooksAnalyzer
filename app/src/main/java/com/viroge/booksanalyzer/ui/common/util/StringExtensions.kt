package com.viroge.booksanalyzer.ui.common.util

/**
 * Truncates string to [limit] and optionally appends dots.
 * Example use case: Book Titles shown in the UI.
 */
fun String.truncate(
    limit: Int,
    appendDots: Boolean = true,
): String = if (this.length <= limit) this else {
    val suffix = if (appendDots) "..." else ""
    this.take(limit).trimEnd() + suffix
}
