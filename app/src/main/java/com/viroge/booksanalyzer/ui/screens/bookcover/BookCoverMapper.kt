package com.viroge.booksanalyzer.ui.screens.bookcover

import com.viroge.booksanalyzer.domain.usecase.BookCoverCandidate
import javax.inject.Inject

class BookCoverMapper @Inject constructor() {

    fun map(
        candidate: BookCoverCandidate,
    ): BookCoverState = BookCoverState(
        url = candidate.url,
        headers = candidate.headers,
    )
}