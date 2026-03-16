package com.viroge.booksanalyzer.data.common.util

import com.viroge.booksanalyzer.domain.model.TempBook

object BooksUtil {

    fun normalizeIsbn(input: String): String = input
        .replace(oldValue = "-", newValue = "")
        .replace(oldValue = " ", newValue = "")
        .trim()

    fun titleKey(
        title: String,
        authors: String,
        year: String?,
    ): String {
        val t = normalize(input = title)
        val a = normalize(input = authors.split(",").map { it.trim() }.firstOrNull().orEmpty())
        val y = year ?: ""

        return generateKey(titlePart = t, authorPart = a, yearPart = y)
    }

    fun titleKey(
        title: String,
        authors: List<String>,
        year: String?,
    ): String {
        val t = normalize(input = title)
        val a = normalize(input = authors.firstOrNull().orEmpty())
        val y = year ?: ""

        return generateKey(titlePart = t, authorPart = a, yearPart = y)
    }

    private fun generateKey(
        titlePart: String,
        authorPart: String,
        yearPart: String,
    ): String {
        // if authors/year missing, still stable; duplicates are still likely the same book:
        return listOf(titlePart, authorPart, yearPart).joinToString(separator = "|")
    }

    fun mergeAndRank(list: List<TempBook>): List<TempBook> {
        if (list.isEmpty()) return emptyList()

        // 1) Group by best-available identity key
        val grouped: Map<String, List<TempBook>> = list.groupBy { it.dedupeKey() }

        // 2) Merge duplicates into one "best" book candidate per key
        val merged: List<TempBook> = grouped.values.map { group ->
            group.reduce { acc, next -> acc.mergePreferBetter(other = next) }
        }
        return merged
    }

    private fun TempBook.mergePreferBetter(other: TempBook): TempBook {
        // Prefer non-null / richer fields.
        // Keep the original source/sourceId (doesn't matter much for merged display),
        // but we can keep the one that has "better" metadata as the base.
        val base = chooseBase(book1 = this, book2 = other)
        val extra = if (base === this) other else this

        return base.copy(
            // Merge best-known values
            year = base.year ?: extra.year,
            isbn13 = base.isbn13 ?: extra.isbn13,
            isbn10 = base.isbn10 ?: extra.isbn10,
            coverUrl = base.coverUrl ?: extra.coverUrl,
            authors = if (base.authors.size >= extra.authors.size) base.authors else extra.authors,
            title = if (base.title.length >= extra.title.length) base.title else extra.title
        )
    }

    private fun chooseBase(
        book1: TempBook,
        book2: TempBook,
    ): TempBook {

        // Pick the book with “better” metadata as base.
        fun score(book: TempBook): Int {
            var s = 0
            if (!book.isbn13.isNullOrBlank()) s += 8
            if (!book.isbn10.isNullOrBlank()) s += 4
            if (!book.coverUrl.isNullOrBlank()) s += 3
            if (book.year != null) s += 2
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

    private fun TempBook.dedupeKey(): String {
        // strongest identifiers first
        isbn13?.takeIf { it.isNotBlank() }?.let { return "isbn13:${normalizeIsbn(it)}" }
        isbn10?.takeIf { it.isNotBlank() }?.let { return "isbn10:${normalizeIsbn(it)}" }

        // source-specific stable IDs
        sourceId?.let { return "src:${source.name}:${sourceId.trim()}" }

        // fallback: stable titleKey (title + first author + year)
        return "ta:${titleKey(title, authors, year)}"
    }
}