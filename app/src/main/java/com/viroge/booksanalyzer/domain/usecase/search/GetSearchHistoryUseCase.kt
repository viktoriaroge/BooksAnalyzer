package com.viroge.booksanalyzer.domain.usecase.search

import com.viroge.booksanalyzer.data.repository.search.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSearchHistoryUseCase @Inject constructor(
    private val historyRepo: SearchHistoryRepository,
) {
    operator fun invoke(limit: Int = 10): Flow<List<String>> =
        historyRepo.observeRecent(limit)
}
