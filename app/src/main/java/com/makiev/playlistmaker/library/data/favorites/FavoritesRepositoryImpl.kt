package com.makiev.playlistmaker.library.data.favorites

import com.makiev.playlistmaker.database.AppDatabase
import com.makiev.playlistmaker.database.TrackEntity
import com.makiev.playlistmaker.database.convertors.TrackDbConvertor
import com.makiev.playlistmaker.library.domain.favorites.api.FavoritesRepository
import com.makiev.playlistmaker.search.domain.models.Track
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