package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.repository.ThemeMode
import com.example.data.repository.UserPreferencesRepository
import com.example.presentation.navigation.AppNavigation
import com.example.ui.theme.CyberGuardTheme

class MainActivity : ComponentActivity() {

    private lateinit var preferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preferencesRepository = UserPreferencesRepository(this)

        setContent {
            val prefs by preferencesRepository.preferences.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()

            val isDarkTheme = when (prefs.themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> systemDark
            }

            CyberGuardTheme(darkTheme = isDarkTheme) {
                AppNavigation()
            }
        }
    }
}
