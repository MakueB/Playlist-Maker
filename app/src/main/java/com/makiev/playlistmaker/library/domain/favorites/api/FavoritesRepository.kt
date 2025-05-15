package com.makiev.playlistmaker.library.domain.favorites.api

import com.makiev.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track: Track)
    suspend fun getFavoritesAll(): Flow<List<Track>>
}