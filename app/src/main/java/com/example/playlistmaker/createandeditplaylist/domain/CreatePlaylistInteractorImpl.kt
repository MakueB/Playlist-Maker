package com.example.playlistmaker.createandeditplaylist.domain

import com.example.playlistmaker.createandeditplaylist.domain.api.CreatePlaylistInteractor
import com.example.playlistmaker.createandeditplaylist.domain.api.CreatePlaylistRepository
import com.example.playlistmaker.createandeditplaylist.domain.models.Playlist

class CreatePlaylistInteractorImpl(
    private val createPlaylistRepository: CreatePlaylistRepository)
    : CreatePlaylistInteractor {
    override suspend fun savePlaylist(playlist: Playlist) {
        createPlaylistRepository.savePlaylist(playlist)
    }
}