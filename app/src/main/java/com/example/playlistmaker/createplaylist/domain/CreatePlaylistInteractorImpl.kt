package com.example.playlistmaker.createplaylist.domain

import com.example.playlistmaker.createplaylist.domain.api.CreatePlaylistInteractor
import com.example.playlistmaker.createplaylist.domain.api.CreatePlaylistRepository
import com.example.playlistmaker.createplaylist.domain.models.Playlist

class CreatePlaylistInteractorImpl(
    private val createPlaylistRepository: CreatePlaylistRepository)
    : CreatePlaylistInteractor {
    override suspend fun savePlaylist(playlist: Playlist) {
        createPlaylistRepository.savePlaylist(playlist)
    }
}