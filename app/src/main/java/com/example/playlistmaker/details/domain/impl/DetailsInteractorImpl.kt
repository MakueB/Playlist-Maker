package com.example.playlistmaker.details.domain.impl

import com.example.playlistmaker.details.domain.api.DetailsInteractor
import com.example.playlistmaker.details.domain.api.DetailsRepository
import com.example.playlistmaker.details.ui.ShareCommand
import com.example.playlistmaker.details.ui.models.PlaylistUiModel

class DetailsInteractorImpl(
    private val repository: DetailsRepository
) : DetailsInteractor {

    override fun getShareCommand(playlist: PlaylistUiModel): ShareCommand {
        return repository.getShareCommand(playlist)
    }
}
