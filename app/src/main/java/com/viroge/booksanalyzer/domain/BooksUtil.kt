package com.viroge.booksanalyzer.domain

import com.viroge.booksanalyzer.domain.model.Book

object BooksUtil {

    fun normalizeForManualInput(
        string: String,
        delimiter: String = " ",
        separator: String = " ",
    ): String {
        return string.split(delimiter)
            .joinToString(separator = separator) { word ->
                val lowercaseWord = word.lowercase()
                if (lowercaseWord.length > 3) lowercaseWord.replaceFirstChar { char -> char.titlecase() } else lowercaseWord
            }
            .replaceFirstChar { char -> char.titlecase() } // always have the first word be capitalized
    }

    fun normalizeIsbn(input: String): String = input
        .replace(oldValue = "-", newValue = "")
        .replace(oldValue = " ", newValue = "")
        .trim()

    fun splitIsbns(
        isbns: List<String>,
    ): Pair<String?, String?> {
        val isbn13 = isbns.firstOrNull { it.length == 13 }
        val isbn10 = isbns.firstOrNull { it.length == 10 }
        return isbn13 to isbn10
    }

    fun titleKey(
        title: String,
        authors: List<String>,
        year: String?,
    ): String {
        val t = normalize(input = title)
        val a = normalize(input = authors.firstOrNull().orEmpty())
        val y = year ?: ""

        // if authors/year missing, still stable; duplicates are still likely the same book:
        return listOf(t, a, y).joinToString(separator = "|")
    }

    fun mergeAndRank(list: List<Book>): List<Book> {
        if (list.isEmpty()) return emptyList()

        // 1) Group by best-available identity key
        val grouped: Map<String, List<Book>> = list.groupBy { it.dedupeKey() }

        // 2) Merge duplicates into one "best" book candidate per key
        val merged: List<Book> = grouped.values.map { group ->
            group.reduce { acc, next -> acc.mergePreferBetter(other = next) }
        }
        return merged
    }

    private fun Book.mergePreferBetter(other: Book): Book {
        // Prefer non-null / richer fields.
        // Keep the original source/sourceId (doesn't matter much for merged display),
        // but we can keep the one that has "better" metadata as the base.
        val base = chooseBase(book1 = this, book2 = other)
        val extra = if (base === this) other else this

        return base.copy(
            // Merge best-known values
            publishedYear = base.publishedYear ?: extra.publishedYear,
            isbn13 = base.isbn13 ?: extra.isbn13,
            isbn10 = base.isbn10 ?: extra.isbn10,
            coverUrl = base.coverUrl ?: extra.coverUrl,
            authors = if (base.authors.size >= extra.authors.size) base.authors else extra.authors,
            title = if (base.title.length >= extra.title.length) base.title else extra.title
        )
    }

    private fun chooseBase(
        book1: Book,
        book2: Book,
    ): Book {

        // Pick the book with “better” metadata as base.
        fun score(book: Book): Int {
            var s = 0
            if (!book.isbn13.isNullOrBlank()) s += 8
            if (!book.isbn10.isNullOrBlank()) s += 4
            if (!book.coverUrl.isNullOrBlank()) s += 3
            if (book.publishedYear != null) s += 2
            s += (book.authors.size.coerceAtMost(maximumValue = 3)) // small boost
            return s
        }
        return if (score(book = book1) >= score(book = book2)) {
            book1
        } else {
            book2
        }
    }

    private fun normalize(
        input: String,
    ): String = input
        .lowercase()
        // keep letters/numbers, drop punctuation:
        .replace(regex = Regex(pattern = """[^\p{L}\p{N}\s]"""), replacement = " ")
        .replace(regex = Regex(pattern = """\s+"""), replacement = " ")
        .trim()

    private fun Book.dedupeKey(): String {
        // strongest identifiers first
        isbn13?.takeIf { it.isNotBlank() }?.let { return "isbn13:${normalizeIsbn(it)}" }
        isbn10?.takeIf { it.isNotBlank() }?.let { return "isbn10:${normalizeIsbn(it)}" }

        // source-specific stable IDs
        sourceId?.let { return "src:${source.name}:${sourceId.trim()}" }

        // fallback: stable titleKey (title + first author + year)
        return "ta:${titleKey(title, authors, publishedYear)}"
    }
}