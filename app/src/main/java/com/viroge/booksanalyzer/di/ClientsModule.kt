package com.viroge.booksanalyzer.di

import com.viroge.booksanalyzer.data.remote.google.GoogleBooksApi
import com.viroge.booksanalyzer.data.remote.google.GoogleBooksClient
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryApi
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ClientsModule {

    @Provides
    @Singleton
    fun provideGoogleBooksClient(
        api: GoogleBooksApi,
        apiKey: String,
    ): GoogleBooksClient = GoogleBooksClient(api, apiKey)

    @Provides
    @Singleton
    fun provideOpenLibraryClient(
        api: OpenLibraryApi,
    ): OpenLibraryClient = OpenLibraryClient(api)
}
