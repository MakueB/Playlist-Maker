package com.makiev.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import com.makiev.playlistmaker.settings.domain.api.SettingsInteractor
import com.makiev.playlistmaker.settings.domain.model.ThemeSettings
import com.makiev.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel (
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    fun shareApp() {
        sharingInteractor.shareApp()
    }
    fun openTerms() {
        sharingInteractor.openTerms()
    }
    fun openSupport() {
        sharingInteractor.openSupport()
    }
    fun updateTheme(checked: Boolean) {
        settingsInteractor.setDarkThemeEnabled(checked)
        settingsInteractor.updateThemeSettings(ThemeSettings(checked))
    }
}