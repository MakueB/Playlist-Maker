package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.utils.Keys

class App : Application() {
    private lateinit var  sharedPrefs: SharedPreferences

    private val _darkThemeEnabled = MutableLiveData<Boolean>()
    val darkThemeEnabled: LiveData<Boolean> = _darkThemeEnabled

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(Keys.PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        _darkThemeEnabled.value = sharedPrefs.getBoolean(Keys.THEME_KEY, false)
        switchTheme(_darkThemeEnabled.value ?: false)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        _darkThemeEnabled.value = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
