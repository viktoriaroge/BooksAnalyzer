package com.viroge.booksanalyzer.util

object BookKeys {

    fun titleKey(
        title: String,
        authors: List<String>,
        year: Int?,
    ): String {
        val t = normalize(title)
        val a = normalize(authors.firstOrNull().orEmpty())
        val y = year?.toString().orEmpty()

        // if authors/year missing, still stable; duplicates are still likely the same book:
        return listOf(t, a, y).joinToString("|")
    }

    private fun normalize(
        input: String,
    ): String = input
        .lowercase()
        // keep letters/numbers, drop punctuation:
        .replace(Regex("""[^\p{L}\p{N}\s]"""), " ")
        .replace(Regex("""\s+"""), " ")
        .trim()
}
