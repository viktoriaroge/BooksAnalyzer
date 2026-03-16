package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.data.BooksRepository
import javax.inject.Inject

class GetBookCoverHeadersUseCase @Inject constructor(
    private val booksRepository: BooksRepository,
) {

    operator fun invoke(url: String): Map<String, String> {
        return booksRepository.getBookCoverHeaders(url)
    }
}
