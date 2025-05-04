package com.example.playlistmaker.library.domain.playlists.api

import com.example.playlistmaker.createplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun getPlaylistsAll(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlistId: Long) : Flow<Resource<String>>
    suspend fun removeTrackFromPlaylist(track: Int, playlistId: Long)
    suspend fun clearPlaylist(playlistId: Long)
}