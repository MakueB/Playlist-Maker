package com.makiev.playlistmaker.details.domain.api

import com.makiev.playlistmaker.details.ui.ShareCommand
import com.makiev.playlistmaker.details.ui.models.PlaylistUiModel

interface DetailsRepository {
    fun getShareCommand(playlistUiModel: PlaylistUiModel): ShareCommand
}
