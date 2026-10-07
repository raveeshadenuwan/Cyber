package com.example.presentation.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.repository.ThemeMode
import com.example.data.repository.UserPreferences
import com.example.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserPreferencesRepository(application)
    val preferences: StateFlow<UserPreferences> = repository.preferences

    fun setThemeMode(mode: ThemeMode) {
        repository.setThemeMode(mode)
    }

    fun setSecurityReminders(enabled: Boolean) {
        repository.setSecurityReminders(enabled)
    }

    fun setScanApps(enabled: Boolean) {
        repository.setScanApps(enabled)
    }

    fun setScanNetwork(enabled: Boolean) {
        repository.setScanNetwork(enabled)
    }
}
