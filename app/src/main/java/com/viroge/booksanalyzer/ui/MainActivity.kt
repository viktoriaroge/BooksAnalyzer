package com.viroge.booksanalyzer.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.viroge.booksanalyzer.ui.nav.AppRoot
import com.viroge.booksanalyzer.ui.theme.BooksAnalyzerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        // Install splash screen:
        val splashScreen = installSplashScreen()
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            // 1. Safely grab the icon view
            val iconView = try {
                splashScreenView.iconView
            } catch (e: Exception) {
                null
            }

            if (iconView != null) {
                val scaleX = ObjectAnimator.ofFloat(iconView, View.SCALE_X, 1f, 5f)
                val scaleY = ObjectAnimator.ofFloat(iconView, View.SCALE_Y, 1f, 5f)
                val alpha = ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)

                AnimatorSet().apply {
                    duration = 400L
                    playTogether(scaleX, scaleY, alpha)
                    doOnEnd { splashScreenView.remove() }
                    start()
                }
            } else {
                // 2. Fallback: If iconView is null, just fade out the whole splash view
                splashScreenView.view.animate()
                    .alpha(0f)
                    .setDuration(400L)
                    .withEndAction { splashScreenView.remove() }
                    .start()
            }
        }

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Keep on until DB maintenance is done:
        splashScreen.setKeepOnScreenCondition { viewModel.isLoading.value }

        setContent {
            BooksAnalyzerTheme {
                AppRoot()
            }
        }
    }
}