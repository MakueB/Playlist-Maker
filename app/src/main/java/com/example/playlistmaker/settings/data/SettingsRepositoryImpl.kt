package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.utils.Keys

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) : SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        val isDarkModeEnabled = sharedPreferences.getBoolean(Keys.THEME_KEY, false)
        return ThemeSettings(isDarkModeEnabled)
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        sharedPreferences.edit().putBoolean(Keys.THEME_KEY, settings.isDarkModeEnabled).apply()
    }
}