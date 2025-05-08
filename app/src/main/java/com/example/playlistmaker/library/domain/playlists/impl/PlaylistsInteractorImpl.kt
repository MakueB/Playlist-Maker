package com.example.playlistmaker.library.domain.playlists.impl

import com.example.playlistmaker.library.domain.playlists.api.PlaylistsInteractor
import com.example.playlistmaker.library.domain.playlists.api.PlaylistsRepository
import com.example.playlistmaker.createandeditplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsInteractorImpl (
    private val repository: PlaylistsRepository
) : PlaylistsInteractor {
    override suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }

    override suspend fun getPlaylistsAll(): Flow<List<Playlist>> {
        return repository.getPlaylistsAll()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long) : Flow<Pair<String, Boolean>> = flow {
        repository.addTrackToPlaylist(track, playlistId).collect{ result ->
            when (result) {
                is Resource.Success -> emit(Pair(result.data ?: "", true))  // Success case
                is Resource.Error -> emit(Pair(result.message ?: "", false))  // Error case
            }
        }
    }

    override suspend fun removeTrackFromPlaylist(track: Track, playlistId: Long) {
        repository.removeTrackFromPlaylist(track.trackId, playlistId)
    }

    override suspend fun clearPlaylist(playlistId: Long) {
        repository.clearPlaylist(playlistId)
    }

}