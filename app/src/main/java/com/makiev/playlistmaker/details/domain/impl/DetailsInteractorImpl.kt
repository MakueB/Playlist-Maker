package com.makiev.playlistmaker.details.domain.impl

import com.makiev.playlistmaker.details.domain.api.DetailsInteractor
import com.makiev.playlistmaker.details.domain.api.DetailsRepository
import com.makiev.playlistmaker.details.ui.ShareCommand
import com.makiev.playlistmaker.details.ui.models.PlaylistUiModel
import com.makiev.playlistmaker.library.domain.playlists.api.PlaylistsRepository

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
