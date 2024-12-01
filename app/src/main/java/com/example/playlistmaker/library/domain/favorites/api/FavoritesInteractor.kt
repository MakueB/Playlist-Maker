package com.example.playlistmaker.library.domain.favorites.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track: Track)
    suspend fun getFavoritesAll(): Flow<List<Track>>
}