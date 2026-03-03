package com.viroge.booksanalyzer.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.viroge.booksanalyzer.data.remote.BookCoverHeaders
import com.viroge.booksanalyzer.data.remote.google.GoogleBooksApi
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideConverterFactory(json: Json): Converter.Factory =
        json.asConverterFactory("application/json".toMediaType())

    @Provides
    @Singleton
    fun provideBookCoverHeaders(): BookCoverHeaders = BookCoverHeaders()

    @Provides
    @Singleton
    @GoogleBooksRetrofit
    fun provideGoogleRetrofit(
        converter: Converter.Factory,
        logging: HttpLoggingInterceptor,
        headers: BookCoverHeaders,
    ): Retrofit {
        val okHttp = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                // NOTE: Google Books API authentication is multi-layered. :)
                // It requires the package name and the SHA1 hash as headers.
                // And later also the generated API key as a query parameter.
                val requestBuilder = chain.request().newBuilder()
                val headersToAdd = headers.getGoogleBooksHeaders()
                for ((name, value) in headersToAdd) {
                    requestBuilder.header(name, value)
                }
                chain.proceed(requestBuilder.build())
            })
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .client(okHttp)
            .addConverterFactory(converter)
            .build()
    }

    @Provides
    @Singleton
    @OpenLibraryRetrofit
    fun provideOpenLibraryRetrofit(
        converter: Converter.Factory,
        logging: HttpLoggingInterceptor,
        headers: BookCoverHeaders,
    ): Retrofit {
        val okHttp = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                // NOTE: Adding a user email in the header helps us get more allowed requests per second.
                // For Open Library API that raises our requests from 1 to 3 per second.
                val requestBuilder = chain.request().newBuilder()
                val headersToAdd = headers.getOpenLibraryHeaders()
                for ((name, value) in headersToAdd) {
                    requestBuilder.header(name, value)
                }
                chain.proceed(requestBuilder.build())
            })
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://openlibrary.org/")
            .client(okHttp)
            .addConverterFactory(converter)
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleBooksApi(
        @GoogleBooksRetrofit retrofit: Retrofit,
    ): GoogleBooksApi = retrofit.create(GoogleBooksApi::class.java)

    @Provides
    @Singleton
    fun provideOpenLibraryApi(
        @OpenLibraryRetrofit retrofit: Retrofit,
    ): OpenLibraryApi = retrofit.create(OpenLibraryApi::class.java)
}
