package com.viroge.booksanalyzer.data

import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {

    fun observeRecent(
        limit: Int,
    ): Flow<List<String>>

    suspend fun recordQuery(
        query: String,
        limit: Int,
    )

    suspend fun deleteQuery(query: String)

    suspend fun clearAll()
}
