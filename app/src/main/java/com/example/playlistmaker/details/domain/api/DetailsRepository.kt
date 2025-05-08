package com.example.playlistmaker.details.domain.api

import com.example.playlistmaker.details.ui.ShareCommand
import com.example.playlistmaker.details.ui.models.PlaylistUiModel

interface DetailsRepository {
    fun getShareCommand(playlistUiModel: PlaylistUiModel): ShareCommand
}
