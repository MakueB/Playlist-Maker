package com.example.playlistmaker

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val prefs: SharedPreferences) {
    fun saveToPrefs(trackList: List<Track>) {
        val gson = Gson()
        val json = gson.toJson(trackList)
        prefs.edit { putString(Keys.SEARCH_HISTORY_KEY, json) }
    }

    fun loadFromPrefs(): List<Track> {
        val history: MutableList<Track>? =
            Gson().fromJson(prefs.getString(Keys.SEARCH_HISTORY_KEY, null),
                            object : TypeToken<List<Track>>() {}.type
            )

        return if (!history.isNullOrEmpty()) history else mutableListOf()
    }
}