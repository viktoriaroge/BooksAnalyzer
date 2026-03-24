package com.viroge.booksanalyzer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Temp book is a means to save a temporary book copy when the copy is
 * not yet kept in the DB and cannot be obtained from there.
 * NOTE:
 * This is an unreliable source, currently used for search results.
 */
@Serializable
@Parcelize
data class TempBook(
    val animationKey: String,
    val source: BookSource,
    val sourceId: String?,
    val title: String,
    val authors: List<String>,
    val year: String?,
    val isbn13: String?,
    val isbn10: String?,
    val originalCoverUrl: String?,
    val coverUrl: String?,
) : Parcelable
