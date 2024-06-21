package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.api.FavoritesInteractor
import com.example.playlistmaker.library.domain.api.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val repository: FavoritesRepository) : FavoritesInteractor {
    override suspend fun addToFavorites(track: Track) {
        repository.addToFavorites(track)
    }

    override suspend fun removeFromFavorites(track: Track) {
        repository.removeFromFavorites(track)
    }

    override suspend fun getFavoritesAll(): Flow<List<Track>> {
        return repository.getFavoritesAll()
    }

}