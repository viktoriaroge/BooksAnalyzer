package com.viroge.booksanalyzer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.viroge.booksanalyzer.ui.nav.AppRoot
import com.viroge.booksanalyzer.ui.theme.BooksAnalyzerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            BooksAnalyzerTheme {
                AppRoot()
            }
        }
    }
}