package com.example.playlistmaker.library.domain.playlists.api

import com.example.playlistmaker.newplaylist.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun getPlaylistsAll(): Flow<List<Playlist>>
}