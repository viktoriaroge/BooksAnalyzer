package com.viroge.booksanalyzer.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.data.sync.book.DeleteBooksScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartupViewModel @Inject constructor(
    private val deleteBooksScheduler: DeleteBooksScheduler,
) : ViewModel() {

    private val _isSplashTimerFinished = MutableStateFlow(false)
    private val _isDoneCleaningUp = MutableStateFlow(false)

    val isLoading = combine(
        _isDoneCleaningUp,
        _isSplashTimerFinished
    ) { ready, timedOut -> !(ready && timedOut) }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly, // start as soon as the view model is created (the DB cleanup needs no UI)
            initialValue = true,
        )

    init {
        viewModelScope.launch {
            // Run cleanup and timer in parallel:
            launch {
                try {
                    deleteBooksScheduler.enqueueBulkDelete()
                } catch (e: Exception) {
                    Log.e("StartupViewModel", "Cleanup failed", e)
                } finally {
                    _isDoneCleaningUp.value = true
                }
            }

            launch {
                delay(1000)
                _isSplashTimerFinished.value = true
            }
        }
    }
}
