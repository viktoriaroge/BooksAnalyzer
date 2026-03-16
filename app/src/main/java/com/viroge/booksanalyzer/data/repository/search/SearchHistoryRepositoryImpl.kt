package com.viroge.booksanalyzer.data.repository.search

import com.viroge.booksanalyzer.data.local.searchhistory.SearchHistoryDao
import com.viroge.booksanalyzer.data.local.searchhistory.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepositoryImpl @Inject constructor(
    private val dao: SearchHistoryDao,
) : SearchHistoryRepository {

    override fun observeRecent(
        limit: Int,
    ): Flow<List<String>> = dao.observeRecent(limit).map {
        it.map { row -> row.query }
    }

    override suspend fun recordQuery(
        query: String,
        limit: Int,
    ) {
        val q = query.trim()
        if (q.length < 2) return

        dao.upsert(
            item = SearchHistoryEntity(
                query = q,
                lastUsedEpochMs = System.currentTimeMillis(),
            )
        )
        dao.trimTo(limit)
    }

    override suspend fun deleteQuery(query: String) = dao.delete(query)

    override suspend fun clearAll() = dao.clearAll()
}
