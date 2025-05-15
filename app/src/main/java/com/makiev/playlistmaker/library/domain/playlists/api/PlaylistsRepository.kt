package com.makiev.playlistmaker.library.domain.playlists.api

import com.makiev.playlistmaker.createandeditplaylist.domain.models.Playlist
import com.makiev.playlistmaker.search.domain.models.Track
import com.makiev.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun getPlaylistsAll(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlistId: Long) : Flow<Resource<String>>
    suspend fun removeTrackFromPlaylist(track: Int, playlistId: Long)
    suspend fun clearPlaylist(playlistId: Long)
}