package com.viroge.booksanalyzer.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.viroge.booksanalyzer.domain.model.BookCoverDataSeed
import com.viroge.booksanalyzer.domain.model.BookSeed
import com.viroge.booksanalyzer.domain.model.TempBook
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore by preferencesDataStore(name = "user_session_prefs")

@Singleton
class UserSelectionRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : UserSelectionRepository {
    private val json = Json { ignoreUnknownKeys = true }

    private object Keys {
        val TEMP_BOOK = stringPreferencesKey("selected_temp_book")
        val BOOK_SEED = stringPreferencesKey("selected_book_seed")
        val COVER_URL = stringPreferencesKey("selected_cover_url")
        val BOOK_COVER_SEED = stringPreferencesKey("selected_book_cover_seed")
    }

    override val selectedTempBook: Flow<TempBook?> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.TEMP_BOOK]?.let { json.decodeFromString<TempBook>(it) }
    }

    override val selectedBookSeed: Flow<BookSeed?> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.BOOK_SEED]?.let { json.decodeFromString<BookSeed>(it) }
    }

    override val selectedCoverUrl: Flow<String?> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.COVER_URL]
    }

    override val selectedBookCoverDataSeed: Flow<BookCoverDataSeed?> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.BOOK_COVER_SEED]?.let { json.decodeFromString<BookCoverDataSeed>(it) }
    }

    override suspend fun updateTempBook(book: TempBook?) {
        context.sessionDataStore.edit { prefs ->
            if (book == null) prefs.remove(Keys.TEMP_BOOK)
            else prefs[Keys.TEMP_BOOK] = json.encodeToString(book)
        }
    }

    override suspend fun updateBookSeed(seed: BookSeed?) {
        context.sessionDataStore.edit { prefs ->
            if (seed == null) prefs.remove(Keys.BOOK_SEED)
            else prefs[Keys.BOOK_SEED] = json.encodeToString(seed)
        }
    }

    override suspend fun updateCoverUrl(url: String?) {
        context.sessionDataStore.edit { prefs ->
            if (url == null) prefs.remove(Keys.COVER_URL)
            else prefs[Keys.COVER_URL] = url
        }
    }

    override suspend fun updateBookCoverSeed(seed: BookCoverDataSeed?) {
        context.sessionDataStore.edit { prefs ->
            if (seed == null) prefs.remove(Keys.BOOK_COVER_SEED)
            else prefs[Keys.BOOK_COVER_SEED] = json.encodeToString(seed)
        }
    }
}
