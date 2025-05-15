package com.makiev.playlistmaker.details.ui

import com.makiev.playlistmaker.details.ui.models.PlaylistUiModel

sealed class DetailsUiState {
    object Loading : DetailsUiState()
    data class Content(val playlist: PlaylistUiModel) : DetailsUiState()
}
