package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun search(query: String): Flow<Pair<List<Track>?, String?>>
    fun saveToHistory(track: Track)
    fun getSearchHistory() : List<Track>
    fun clearHistory()
}