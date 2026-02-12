package com.viroge.booksanalyzer.data.local.searchhistory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Query(value = "SELECT * FROM search_history ORDER BY lastUsedEpochMs DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: SearchHistoryEntity)

    @Query(value = "DELETE FROM search_history WHERE query = :query")
    suspend fun delete(query: String)

    @Query(value = "DELETE FROM search_history")
    suspend fun clearAll()

    // Keep only the newest N rows
    @Query(
        value = """
                DELETE FROM search_history
                WHERE `query` NOT IN (
                  SELECT `query` FROM search_history
                  ORDER BY lastUsedEpochMs DESC
                  LIMIT :limit
                )
            """
    )
    suspend fun trimTo(limit: Int)
}
