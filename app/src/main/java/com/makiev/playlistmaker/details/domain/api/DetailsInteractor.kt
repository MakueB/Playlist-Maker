package com.makiev.playlistmaker.details.domain.api

import com.makiev.playlistmaker.details.ui.ShareCommand
import com.makiev.playlistmaker.details.ui.models.PlaylistUiModel

interface DetailsInteractor {
    fun getShareCommand(playlist: PlaylistUiModel): ShareCommand
    suspend fun removeTrackFromPlaylist(trackId: Int, playlistId: Long)
}
