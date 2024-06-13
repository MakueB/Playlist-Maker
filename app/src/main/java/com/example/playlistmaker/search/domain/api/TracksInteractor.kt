package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun search(query: String): Flow<Pair<List<Track>?, String?>>
    suspend fun saveToHistory(track: Track)
    suspend fun getSearchHistory() : List<Track>
    fun clearHistory()
}