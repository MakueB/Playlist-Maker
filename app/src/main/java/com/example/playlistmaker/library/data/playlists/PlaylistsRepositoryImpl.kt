package com.example.playlistmaker.library.data.playlists

import com.example.playlistmaker.database.PlaylistEntity
import com.example.playlistmaker.database.PlaylistTrackEntity
import com.example.playlistmaker.database.convertors.EmptyPlaylistDbConvertor
import com.example.playlistmaker.database.convertors.TrackDbConvertor
import com.example.playlistmaker.database.dao.PlaylistDao
import com.example.playlistmaker.database.dao.PlaylistTrackDao
import com.example.playlistmaker.library.domain.playlists.api.PlaylistsRepository
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val playlistDbConvertor: EmptyPlaylistDbConvertor,
    private val trackDbConvertor: TrackDbConvertor
) : PlaylistsRepository {
    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlistDbConvertor.map(playlist))
    }

    override suspend fun getPlaylistsAll(): Flow<List<Playlist>> = flow {
        val playlistsDb = convertFromEntity(playlistDao.getAllPlaylists())
        val playlists = addTracksByPlaylistId(playlistsDb)
        emit(playlists)
    }



    private fun convertFromEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    private suspend fun addTracksByPlaylistId(playlists: List<Playlist>): List<Playlist> {
        return playlists.map { playlist: Playlist ->
            playlist.copy(trackList = convertFromTrackEntity(playlistTrackDao.getTracksByPlaylist(playlist.id)))
        }
    }

    private fun convertFromTrackEntity(tracks: List<PlaylistTrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.mapFromPlaylistTrackEntity(track) }
    }
}