package com.viroge.booksanalyzer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.viroge.booksanalyzer.ui.nav.AppNavHost
import com.viroge.booksanalyzer.ui.theme.BooksAnalyzerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BooksAnalyzerTheme { // TODO: apply theme to screens
                AppNavHost()
            }
        }
    }
}
