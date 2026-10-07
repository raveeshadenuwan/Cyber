package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val securityRemindersEnabled: Boolean = true,
    val scanAppsEnabled: Boolean = true,
    val scanNetworkEnabled: Boolean = true
)

class UserPreferencesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("cyberguard_prefs", Context.MODE_PRIVATE)

    private val _preferences = MutableStateFlow(loadPreferences())
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    private fun loadPreferences(): UserPreferences {
        val themeStr = prefs.getString("theme_mode", ThemeMode.DARK.name) ?: ThemeMode.DARK.name
        val theme = try {
            ThemeMode.valueOf(themeStr)
        } catch (e: Exception) {
            ThemeMode.DARK
        }
        val reminders = prefs.getBoolean("security_reminders", true)
        val scanApps = prefs.getBoolean("scan_apps", true)
        val scanNet = prefs.getBoolean("scan_network", true)

        return UserPreferences(
            themeMode = theme,
            securityRemindersEnabled = reminders,
            scanAppsEnabled = scanApps,
            scanNetworkEnabled = scanNet
        )
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _preferences.value = _preferences.value.copy(themeMode = mode)
    }

    fun setSecurityReminders(enabled: Boolean) {
        prefs.edit().putBoolean("security_reminders", enabled).apply()
        _preferences.value = _preferences.value.copy(securityRemindersEnabled = enabled)
    }

    fun setScanApps(enabled: Boolean) {
        prefs.edit().putBoolean("scan_apps", enabled).apply()
        _preferences.value = _preferences.value.copy(scanAppsEnabled = enabled)
    }

    fun setScanNetwork(enabled: Boolean) {
        prefs.edit().putBoolean("scan_network", enabled).apply()
        _preferences.value = _preferences.value.copy(scanNetworkEnabled = enabled)
    }
}
