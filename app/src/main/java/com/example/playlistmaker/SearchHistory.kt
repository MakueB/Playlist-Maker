package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(val prefs: SharedPreferences) {
    /*    private val historyTrackList = mutableListOf<Track>()

        fun add(track: Track){
            val position = historyTrackList.indexOf(track)

            if (historyTrackList.contains(track)){
                historyTrackList.removeAt(position)
                historyTrackList.add(0,track)
            }

            if (historyTrackList.size > 10)
                historyTrackList.remove(historyTrackList.lastOrNull())

        }

        fun clear(){
            historyTrackList.clear()
        }*/

    fun saveToPrefs(trackList: MutableList<Track>) {
        val gson = Gson()
        val json = gson.toJson(trackList)
        prefs.edit().putString(Keys.SEARCH_HISTORY_KEY, json).apply()
    }

    fun loadFromPrefs(): MutableList<Track> {
        val gson = Gson()
        val json = prefs.getString(Keys.SEARCH_HISTORY_KEY, null)
        val history: MutableList<Track>? =
            gson.fromJson(json, object : TypeToken<List<Track>>() {}.type)

        if (!history.isNullOrEmpty())
            return history
        else
            return mutableListOf()
    }
}