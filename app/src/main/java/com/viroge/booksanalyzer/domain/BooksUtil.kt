package com.viroge.booksanalyzer.domain

object BooksUtil {

    fun cacheKey(mode: String, query: String, limit: Int): String {
        val normalized = query
            .trim()
            .lowercase()
            .replace(regex = Regex(pattern = """\s+"""), replacement = " ")

        return "$mode|$limit|$normalized"
    }

    fun titleKey(
        title: String,
        authors: List<String>,
        year: Int?,
    ): String {
        val t = normalize(input = title)
        val a = normalize(input = authors.firstOrNull().orEmpty())
        val y = year?.toString().orEmpty()

        // if authors/year missing, still stable; duplicates are still likely the same book:
        return listOf(t, a, y).joinToString(separator = "|")
    }

    fun mergeAndRank(list: List<BookCandidate>): List<BookCandidate> {
        if (list.isEmpty()) return emptyList()

        // 1) Group by best-available identity key
        val grouped: Map<String, List<BookCandidate>> = list.groupBy { it.dedupeKey() }

        // 2) Merge duplicates into one "best" candidate per key
        val merged: List<BookCandidate> = grouped.values.map { group ->
            group.reduce { acc, next -> acc.mergePreferBetter(other = next) }
        }
        return merged
    }

    private fun BookCandidate.mergePreferBetter(other: BookCandidate): BookCandidate {
        // Prefer non-null / richer fields.
        // Keep the original source/sourceId (doesn't matter much for merged display),
        // but we can keep the one that has "better" metadata as the base.
        val base = chooseBase(candidate1 = this, candidate2 = other)
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
        candidate1: BookCandidate,
        candidate2: BookCandidate,
    ): BookCandidate {

        // Pick the candidate with “better” metadata as base.
        fun score(candidate: BookCandidate): Int {
            var s = 0
            if (!candidate.isbn13.isNullOrBlank()) s += 8
            if (!candidate.isbn10.isNullOrBlank()) s += 4
            if (!candidate.coverUrl.isNullOrBlank()) s += 3
            if (candidate.publishedYear != null) s += 2
            s += (candidate.authors.size.coerceAtMost(maximumValue = 3)) // small boost
            return s
        }
        return if (score(candidate = candidate1) >= score(candidate = candidate2)) {
            candidate1
        } else {
            candidate2
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

    private fun BookCandidate.dedupeKey(): String {
        // strongest identifiers first
        isbn13?.takeIf { it.isNotBlank() }?.let { return "isbn13:${it.normalizeIsbn()}" }
        isbn10?.takeIf { it.isNotBlank() }?.let { return "isbn10:${it.normalizeIsbn()}" }

        // source-specific stable IDs
        if (sourceId.isNotBlank()) return "src:${source.name}:${sourceId.trim()}"

        // fallback: stable titleKey (title + first author + year)
        return "ta:${titleKey(title, authors, publishedYear)}"
    }

    private fun String.normalizeIsbn(): String =
        replace(oldValue = "-", newValue = "")
            .replace(oldValue = " ", newValue = "")
            .trim()
}