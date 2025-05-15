package com.makiev.playlistmaker.createandeditplaylist.domain

import com.makiev.playlistmaker.createandeditplaylist.domain.api.CreatePlaylistInteractor
import com.makiev.playlistmaker.createandeditplaylist.domain.api.CreatePlaylistRepository
import com.makiev.playlistmaker.createandeditplaylist.domain.models.Playlist

class CreatePlaylistInteractorImpl(
    private val createPlaylistRepository: CreatePlaylistRepository
)
    : CreatePlaylistInteractor {
    override suspend fun savePlaylist(playlist: Playlist) {
        createPlaylistRepository.savePlaylist(playlist)
    }
}