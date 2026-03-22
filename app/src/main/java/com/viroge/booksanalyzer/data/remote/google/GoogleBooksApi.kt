package com.viroge.booksanalyzer.data.remote.google

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApi {

    @GET("volumes")
    suspend fun searchVolumes(
        @Query("q") query: String,
        @Query("startIndex") startIndex: Int,
        @Query("maxResults") maxResults: Int,
        @Query("key") apiKey: String,
    ): GoogleVolumesResponse
}

@Serializable
data class GoogleVolumesResponse(
    val items: List<GoogleVolumeItem> = emptyList(),
)

@Serializable
data class GoogleVolumeItem(
    val id: String,
    val volumeInfo: GoogleVolumeInfo = GoogleVolumeInfo(),
)

@Serializable
data class GoogleVolumeInfo(
    val title: String = "",
    val authors: List<String> = emptyList(),
    val description: String = "",
    val publisher: String = "",
    val publishedDate: String? = null,
    val industryIdentifiers: List<GoogleIndustryIdentifier> = emptyList(),
    val pageCount: Int? = null,
)

@Serializable
data class GoogleIndustryIdentifier(
    val type: String = "",
    val identifier: String = "",
)
