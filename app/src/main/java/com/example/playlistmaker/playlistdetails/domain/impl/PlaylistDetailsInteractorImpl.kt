package com.example.playlistmaker.playlistdetails.domain.impl

import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.playlistdetails.domain.api.PlaylistDetailsInteractor
import com.example.playlistmaker.playlistdetails.domain.api.PlaylistDetailsRepository
import com.example.playlistmaker.playlistdetails.ui.ShareCommand

class PlaylistDetailsInteractorImpl(
    private val repository: PlaylistDetailsRepository
) : PlaylistDetailsInteractor {

    override fun execute(playlist: Playlist?): ShareCommand {
        return repository.execute(playlist)
    }
}