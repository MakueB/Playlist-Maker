package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
    suspend fun saveToHistory(track: Track)
    suspend fun getSearchHistory() : List<Track>
    fun clearHistory()
}