package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.utils.Keys
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var settingsInteractor: SettingsInteractor
    private lateinit var settingsRepository: SettingsRepository
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

        sharedPrefs = getSharedPreferences(Keys.PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        settingsRepository = SettingsRepositoryImpl(sharedPrefs)
        settingsInteractor = SettingsInteractorImpl(settingsRepository)

        settingsRepository.setDarkThemeEnabled(settingsRepository.isDarkThemeEnabled())
    }
}
