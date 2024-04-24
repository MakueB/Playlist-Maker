package com.example.playlistmaker.settings.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel (
    application: Application,
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : AndroidViewModel(application) {

    private val app by lazy { application as App }
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
        app.switchTheme(checked)
        settingsInteractor.updateThemeSettings(ThemeSettings(checked))
    }
}