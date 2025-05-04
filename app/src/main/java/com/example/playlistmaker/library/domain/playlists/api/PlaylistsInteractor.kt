package com.example.playlistmaker.library.domain.playlists.api

import com.example.playlistmaker.createandeditplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun getPlaylistsAll(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlistId: Long) : Flow<Pair<String, Boolean>>
    suspend fun removeTrackFromPlaylist(track: Track, playlistId: Long)
    suspend fun clearPlaylist(playlistId: Long)
}