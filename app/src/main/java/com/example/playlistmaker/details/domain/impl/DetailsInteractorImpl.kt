package com.example.playlistmaker.details.domain.impl

import com.example.playlistmaker.details.domain.api.DetailsInteractor
import com.example.playlistmaker.details.domain.api.DetailsRepository
import com.example.playlistmaker.details.ui.ShareCommand
import com.example.playlistmaker.details.ui.models.PlaylistUiModel
import com.example.playlistmaker.library.domain.playlists.api.PlaylistsRepository

class DetailsInteractorImpl(
    private val detailsRepository: DetailsRepository,
    private val playlistRepository: PlaylistsRepository
) : DetailsInteractor {

    override fun getShareCommand(playlist: PlaylistUiModel): ShareCommand {
        return detailsRepository.getShareCommand(playlist)
    }

    override suspend fun removeTrackFromPlaylist(trackId: Int, playlistId: Long) {
        playlistRepository.removeTrackFromPlaylist(trackId, playlistId)
    }
}
