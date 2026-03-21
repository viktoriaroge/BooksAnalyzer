package com.viroge.booksanalyzer.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.viroge.booksanalyzer.data.remote.google.GoogleBooksApi
import com.viroge.booksanalyzer.data.remote.google.GoogleBooksMapper
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryApi
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Dns
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.net.InetAddress
import java.util.concurrent.TimeUnit
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
    fun provideBaseOkHttpClient(
        logging: HttpLoggingInterceptor,
        googleBooksMapper: GoogleBooksMapper,
        openLibraryMapper: OpenLibraryMapper,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .dns(object : Dns {
                override fun lookup(hostname: String): List<InetAddress> {
                    val addresses = Dns.SYSTEM.lookup(hostname)
                    if (addresses.size <= 1) return addresses

                    val (ipv4, ipv6) = addresses.partition { it is java.net.Inet4Address }
                    return ipv4 + ipv6 // keep both but start with ipv4, due to issues on some networks
                }
            })
            // TODO: Once the fastFallback functionality is added in follow-up versions of okhttp,
            // TODO: add it here to make the dns resolution even smoother:
            // .fastFallback(true)
            .addInterceptor { chain ->
                val request = chain.request()
                val url = request.url.toString()
                val builder = request.newBuilder()

                // Apply headers based on the destination:
                when {
                    googleBooksMapper.isUrlValid(url) ->
                        googleBooksMapper.getHeaders().forEach { (k, v) -> builder.header(k, v) }

                    openLibraryMapper.isUrlValid(url) ->
                        openLibraryMapper.getHeaders().forEach { (k, v) -> builder.header(k, v) }
                }
                chain.proceed(builder.build())
            }
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    @GoogleBooksRetrofit
    fun provideGoogleRetrofit(
        baseClient: OkHttpClient,
        converter: Converter.Factory,
        googleBooksMapper: GoogleBooksMapper,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(googleBooksMapper.getBaseUrl())
        .client(baseClient)
        .addConverterFactory(converter)
        .build()

    @Provides
    @Singleton
    @OpenLibraryRetrofit
    fun provideOpenLibraryRetrofit(
        baseClient: OkHttpClient,
        converter: Converter.Factory,
        openLibraryMapper: OpenLibraryMapper,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(openLibraryMapper.getBaseUrl())
        .client(baseClient)
        .addConverterFactory(converter)
        .build()

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
