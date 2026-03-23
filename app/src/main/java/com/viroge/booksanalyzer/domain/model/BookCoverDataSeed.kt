package com.viroge.booksanalyzer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Seed book cover data that is stripped down to what the Book Cover Selection logic would need to start.
 */
@Serializable
@Parcelize
data class BookCoverDataSeed(
    val selectedCoverUrl: String?,
    val originalCoverUrl: String?,
    val isbn13: String?,
    val source: BookSource,
    val sourceId: String?,
) : Parcelable
