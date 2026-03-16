package com.viroge.booksanalyzer.data.common.util

import com.viroge.booksanalyzer.domain.model.PageToken

object PageTokenUtil {

    fun makePageToken(
        nextGoogleStart: Int,
        nextOlPage: Int,
    ): String = "g:$nextGoogleStart|ol:$nextOlPage"

    fun parsePageToken(
        token: String?,
    ): PageToken {

        if (token.isNullOrBlank()) return PageToken(googleStart = 0, olPage = 1)

        val parts = token.split("|")
        val g = parts
            .firstOrNull { it.startsWith("g:") }
            ?.removePrefix("g:")
            ?.toIntOrNull() ?: 0
        val ol = parts
            .firstOrNull { it.startsWith("ol:") }
            ?.removePrefix("ol:")
            ?.toIntOrNull() ?: 1
        return PageToken(g, ol)
    }
}