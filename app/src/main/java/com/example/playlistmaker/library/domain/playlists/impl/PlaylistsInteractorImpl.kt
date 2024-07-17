package com.example.playlistmaker.library.domain.playlists.impl

import com.example.playlistmaker.library.data.playlists.PlaylistsRepositoryImpl
import com.example.playlistmaker.library.domain.playlists.api.PlaylistsInteractor
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl (
    private val repository: PlaylistsRepositoryImpl
) : PlaylistsInteractor {
    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override suspend fun getPlaylistsAll(): Flow<List<Playlist>> {
        return repository.getPlaylistsAll()
    }

}