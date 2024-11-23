package com.example.playlistmaker.library.domain.playlists.api

import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun getPlaylistsAll(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlistId: Long)
    suspend fun removeTrackFromPlaylist(track: Int, playlistId: Long)
    suspend fun clearPlaylist(playlistId: Long)
}