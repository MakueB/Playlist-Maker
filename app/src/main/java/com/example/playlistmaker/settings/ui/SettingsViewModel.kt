package com.example.playlistmaker.settings.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.sharing.domain.api.SharingRepository

class SettingsViewModel (
    application: Application,
    private val settingsRepository: SettingsRepository,
    private val sharingRepository: SharingRepository
) : AndroidViewModel(application) {
    companion object{
        fun getViewModelFactory(settingsRepository: SettingsRepository, sharingRepository: SharingRepository) : ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SettingsViewModel(this[APPLICATION_KEY] as Application, settingsRepository, sharingRepository)
                }
            }
    }
    private val app by lazy { application as App }
    fun shareApp() {
        sharingRepository.shareApp()
    }
    fun openTerms() {
        sharingRepository.openTerms()
    }
    fun openSupport() {
        sharingRepository.openSupport()
    }
    fun updateTheme(checked: Boolean) {
        app.switchTheme(checked)
        settingsRepository.updateThemeSettings(ThemeSettings(checked))
    }
}