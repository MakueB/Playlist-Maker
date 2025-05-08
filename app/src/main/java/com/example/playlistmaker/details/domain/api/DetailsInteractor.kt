package com.example.playlistmaker.details.domain.api

import com.example.playlistmaker.details.ui.ShareCommand
import com.example.playlistmaker.details.ui.models.PlaylistUiModel
import com.example.playlistmaker.search.domain.models.Track

interface DetailsInteractor {
    fun getShareCommand(playlist: PlaylistUiModel): ShareCommand
    suspend fun removeTrackFromPlaylist(trackId: Int, playlistId: Long)
}
