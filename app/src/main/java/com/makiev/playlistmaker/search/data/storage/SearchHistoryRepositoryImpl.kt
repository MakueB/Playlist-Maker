package com.makiev.playlistmaker.search.data.storage

import android.content.SharedPreferences
import com.makiev.playlistmaker.database.AppDatabase
import com.makiev.playlistmaker.search.domain.api.SearchHistoryRepository
import com.makiev.playlistmaker.search.domain.models.Track
import com.makiev.playlistmaker.utils.Keys
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl (
    private val gson: Gson,
    private val sharedPreferences: SharedPreferences,
    private val appDatabase: AppDatabase,
) : SearchHistoryRepository {
    override suspend fun saveToHistory(track: Track) {
        val historyList = getSearchHistory().toMutableList()
        historyList.removeAll { it.trackId == track.trackId }
        historyList.add(0, track)

        if (historyList.size > 10) {
            historyList.removeLast()
        }

        sharedPreferences.edit().putString(Keys.Companion.SEARCH_HISTORY_KEY, gson.toJson(historyList)).apply()
    }

    override suspend fun getSearchHistory() : List<Track> {
        val favorites = appDatabase.trackDao().getIdAll()
        var history: MutableList<Track>? =
            gson.fromJson(
                sharedPreferences.getString(Keys.Companion.SEARCH_HISTORY_KEY, null),
                object : TypeToken<List<Track>>() {}.type
            )
        history = history ?: mutableListOf()
        return history.map { it.copy(isFavorite = favorites.contains(it.trackId)) }
    }

    override fun clearHistory() {
        val emptyList: List<Track> = emptyList()
        sharedPreferences.edit().putString(
            Keys.Companion.SEARCH_HISTORY_KEY,
            gson.toJson(emptyList)).apply()
    }
}