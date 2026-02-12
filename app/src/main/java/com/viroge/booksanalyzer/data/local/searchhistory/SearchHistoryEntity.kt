package com.viroge.booksanalyzer.data.local.searchhistory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "query")
    val query: String, // unique by query text

    @ColumnInfo(name = "lastUsedEpochMs")
    val lastUsedEpochMs: Long,
)
