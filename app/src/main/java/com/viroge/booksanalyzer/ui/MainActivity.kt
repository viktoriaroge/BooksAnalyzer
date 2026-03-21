package com.viroge.booksanalyzer.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
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

    private val startupViewModel: StartupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen:
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            startupViewModel.isLoading.value.also { Log.d("MainActivity", "STARTUP: isLoading: $it") }
        }
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            // Try to get the splash center icon and do a fade out animation:
            try {
                splashScreenView.iconView
            } catch (_: Exception) {
                null
            }?.let { iconView ->
                val scaleX = ObjectAnimator.ofFloat(iconView, View.SCALE_X, 1f, 5f)
                val scaleY = ObjectAnimator.ofFloat(iconView, View.SCALE_Y, 1f, 5f)
                val alpha = ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)

                AnimatorSet().apply {
                    duration = 400L
                    playTogether(scaleX, scaleY, alpha)
                    doOnEnd { splashScreenView.remove() }
                    start()
                }
            } ?: {
                splashScreenView.view.animate()
                    .alpha(0f)
                    .setDuration(400L)
                    .withEndAction { splashScreenView.remove() }
                    .start()
            }
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                detectDarkMode = { resources ->
                    (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                            Configuration.UI_MODE_NIGHT_YES
                }
            )
        )

        super.onCreate(savedInstanceState)
        setContent {
            BooksAnalyzerTheme {
                AppRoot()
            }
        }
    }
}
