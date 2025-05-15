package com.makiev.playlistmaker.library.domain.playlists.api

import com.makiev.playlistmaker.createandeditplaylist.domain.models.Playlist
import com.makiev.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun getPlaylistsAll(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlistId: Long) : Flow<Pair<String, Boolean>>
    suspend fun removeTrackFromPlaylist(track: Track, playlistId: Long)
    suspend fun clearPlaylist(playlistId: Long)
}