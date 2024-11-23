package com.example.playlistmaker.library.domain.playlists.impl

import com.example.playlistmaker.library.domain.playlists.api.PlaylistsInteractor
import com.example.playlistmaker.library.domain.playlists.api.PlaylistsRepository
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl (
    private val repository: PlaylistsRepository
) : PlaylistsInteractor {
    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override suspend fun getPlaylistsAll(): Flow<List<Playlist>> {
        return repository.getPlaylistsAll()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long) {

        repository.addTrackToPlaylist(track, playlistId)
    }

    override suspend fun removeTrackFromPlaylist(track: Track, playlistId: Long) {
        repository.removeTrackFromPlaylist(track.trackId, playlistId)
    }

    override suspend fun clearPlaylist(playlistId: Long) {
        repository.clearPlaylist(playlistId)
    }

}