package com.viroge.booksanalyzer.data.remote.openlibrary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApi {

    @GET("search.json")
    suspend fun search(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): OpenLibrarySearchResponse
}

@Serializable
data class OpenLibrarySearchResponse(
    @SerialName("docs") val docs: List<OpenLibraryDoc> = emptyList(),
)

@Serializable
data class OpenLibraryDoc(
    val key: String? = null, // e.g. "/works/OL123W"
    val title: String? = null,

    @SerialName("author_name") val authorName: List<String> = emptyList(),

    // sometimes present:
    @SerialName("first_publish_year") val firstPublishYear: Int? = null,

    // identifiers
    val isbn: List<String> = emptyList(),

    // cover id -> used for cover URLs
    @SerialName("cover_i") val coverId: Int? = null,
)
