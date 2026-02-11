package com.viroge.booksanalyzer.data.remote.google

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApi {

    @GET("volumes")
    suspend fun searchVolumes(
        @Query("q") query: String,
        @Query("startIndex") startIndex: Int = 0,
        @Query("maxResults") maxResults: Int = 10,
        @Query("key") apiKey: String,
    ): VolumesResponse
}

@Serializable
data class VolumesResponse(
    val items: List<VolumeItem> = emptyList(),
)

@Serializable
data class VolumeItem(
    val id: String,
    val volumeInfo: VolumeInfo = VolumeInfo(),
)

@Serializable
data class VolumeInfo(
    val title: String = "",
    val authors: List<String> = emptyList(),
    val publishedDate: String? = null,
    val industryIdentifiers: List<IndustryIdentifier> = emptyList(),
    val imageLinks: ImageLinks? = null,
)

@Serializable
data class IndustryIdentifier(
    val type: String = "",
    val identifier: String = "",
)

@Serializable
data class ImageLinks(
    val thumbnail: String? = null,
    val smallThumbnail: String? = null,
)
