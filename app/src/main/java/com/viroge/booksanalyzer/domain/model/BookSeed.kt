package com.viroge.booksanalyzer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Seed book is stripped down book that needs to later be updated from the DB.
 * Currently used by standard books that have their own ID in the DB.
 */
@Serializable
@Parcelize
data class BookSeed(
    val id: String,
    val url: String,
    val animationKey: String,
) : Parcelable
