package com.makiev.playlistmaker.search.domain.impl

import com.makiev.playlistmaker.search.domain.api.SearchHistoryRepository
import com.makiev.playlistmaker.search.domain.api.TracksInteractor
import com.makiev.playlistmaker.search.domain.api.TracksRepository
import com.makiev.playlistmaker.search.domain.models.Track
import com.makiev.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(
    private val repository: TracksRepository,
    private val historyRepository: SearchHistoryRepository
) : TracksInteractor {
    override fun search(query: String): Flow<Pair<List<Track>?, String?>> {
        return repository.search(query).map { result ->
            when (result) {
                is Resource.Success -> Pair(result.data, null)
                is Resource.Error -> Pair(null, result.message)
            }
        }
    }

    override suspend fun saveToHistory(track: Track) {
        historyRepository.saveToHistory(track)
    }

    override suspend fun getSearchHistory(): List<Track> {
        return historyRepository.getSearchHistory()
    }

    override fun clearHistory() {
        historyRepository.clearHistory()
    }
}