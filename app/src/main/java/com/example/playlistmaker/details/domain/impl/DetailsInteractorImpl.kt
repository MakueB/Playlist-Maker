package com.example.playlistmaker.details.domain.impl

import com.example.playlistmaker.createplaylist.domain.models.Playlist
import com.example.playlistmaker.details.domain.api.DetailsInteractor
import com.example.playlistmaker.details.domain.api.DetailsRepository
import com.example.playlistmaker.details.ui.ShareCommand

class DetailsInteractorImpl(
    private val repository: DetailsRepository
) : DetailsInteractor {

    override fun execute(playlist: Playlist?): ShareCommand {
        return repository.execute(playlist)
    }
}