package com.example.playlistmaker.library.data.favorites

import com.example.playlistmaker.database.AppDatabase
import com.example.playlistmaker.database.TrackEntity
import com.example.playlistmaker.database.convertors.TrackDbConvertor
import com.example.playlistmaker.library.domain.favorites.api.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
    ) : FavoritesRepository {
    override suspend fun addToFavorites(track: Track) {
        appDatabase.trackDao().insertTrack(trackDbConvertor.map(track, -1))
    }

    override suspend fun removeFromFavorites(track: Track) {
        appDatabase.trackDao().deleteTrack(trackDbConvertor.map(track,-1))
    }

    override suspend fun getFavoritesAll(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracksAll()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}