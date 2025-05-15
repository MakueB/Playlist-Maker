package com.makiev.playlistmaker.settings.domain.impl

import com.makiev.playlistmaker.settings.domain.api.SettingsInteractor
import com.makiev.playlistmaker.settings.domain.api.SettingsRepository
import com.makiev.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl (private val settingsRepository: SettingsRepository) :
    SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        settingsRepository.updateThemeSettings(settings)
    }

    override fun isDarkThemeEnabled(): Boolean {
        return settingsRepository.isDarkThemeEnabled()
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        settingsRepository.setDarkThemeEnabled(enabled)
    }

}