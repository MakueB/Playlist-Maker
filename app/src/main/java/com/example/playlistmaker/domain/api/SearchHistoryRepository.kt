package com.example.playlistmaker.domain.api

import android.app.Application
import android.content.Context
import com.example.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun saveToHistory(track: Track)
    fun getSearchHistory() : List<Track>
    fun clearHistory()
}