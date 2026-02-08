package com.viroge.booksanalyzer.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GoogleBooksRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OpenLibraryRetrofit
