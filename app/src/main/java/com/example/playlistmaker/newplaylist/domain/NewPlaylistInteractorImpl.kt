package com.example.playlistmaker.newplaylist.domain

import com.example.playlistmaker.newplaylist.domain.api.NewPlaylistInteractor
import com.example.playlistmaker.newplaylist.domain.api.NewPlaylistRepository
import com.example.playlistmaker.newplaylist.domain.models.Playlist

class NewPlaylistInteractorImpl(
    private val newPlaylistRepository: NewPlaylistRepository)
    : NewPlaylistInteractor {
    override suspend fun savePlaylist(playlist: Playlist) {
        newPlaylistRepository.savePlaylist(playlist)
    }
}