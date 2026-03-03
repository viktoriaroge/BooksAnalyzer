package com.viroge.booksanalyzer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
abstract class ViewModelTest {

    protected val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setupCoroutines() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDownCoroutines() {
        Dispatchers.resetMain()
    }
}
