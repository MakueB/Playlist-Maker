package com.example.playlistmaker.details.ui

import com.example.playlistmaker.details.ui.models.PlaylistUiModel

sealed class DetailsUiState {
    object Loading : DetailsUiState()
    data class Content(val playlist: PlaylistUiModel) : DetailsUiState()
}
