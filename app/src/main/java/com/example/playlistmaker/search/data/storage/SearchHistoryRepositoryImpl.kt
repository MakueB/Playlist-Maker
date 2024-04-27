package com.example.playlistmaker.search.data.storage

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Keys
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl (private val gson: Gson, private val sharedPreferences: SharedPreferences) : SearchHistoryRepository {
    override fun saveToHistory(track: Track) {
        val historyList = getSearchHistory().toMutableList()
        historyList.removeAll { it.trackId == track.trackId }
        historyList.add(0, track)

        if (historyList.size > 10) {
            historyList.removeLast()
        }

        sharedPreferences.edit().putString(Keys.SEARCH_HISTORY_KEY, gson.toJson(historyList)).apply()
    }

    override fun getSearchHistory() : List<Track> {
        val history: MutableList<Track>? =
            gson.fromJson(
                sharedPreferences.getString(Keys.SEARCH_HISTORY_KEY, null),
                object : TypeToken<List<Track>>() {}.type
            )
        return history ?: mutableListOf()
    }

    override fun clearHistory() {
        val emptyList: List<Track> = emptyList()
        sharedPreferences.edit().putString(
            Keys.SEARCH_HISTORY_KEY,
            gson.toJson(emptyList)).apply()
    }
}