package com.example.playlistmaker.details.domain.api

import com.example.playlistmaker.details.ui.ShareCommand
import com.example.playlistmaker.details.ui.models.PlaylistUiModel

interface DetailsInteractor {
    fun getShareCommand(playlist: PlaylistUiModel): ShareCommand
    suspend fun removeTrackFromPlaylist(trackId: Int, playlistId: Long)
}
