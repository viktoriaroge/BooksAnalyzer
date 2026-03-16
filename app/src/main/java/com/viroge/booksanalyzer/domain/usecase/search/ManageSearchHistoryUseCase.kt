package com.viroge.booksanalyzer.domain.usecase.search

import com.viroge.booksanalyzer.data.repository.search.SearchHistoryRepository
import javax.inject.Inject

class ManageSearchHistoryUseCase @Inject constructor(
    private val historyRepo: SearchHistoryRepository,
) {
    suspend fun record(query: String) = historyRepo.recordQuery(query, limit = 10)
    suspend fun delete(query: String) = historyRepo.deleteQuery(query)
    suspend fun clearAll() = historyRepo.clearAll()
}
