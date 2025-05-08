package com.example.playlistmaker.details.ui

import com.example.playlistmaker.search.domain.models.Track

sealed class DetailsUiEvent {
    data class LoadPlaylist(val id: Long) : DetailsUiEvent()
    data class RemoveTrack(val track: Track) : DetailsUiEvent()
    object DeletePlaylist : DetailsUiEvent()
    object ShareClicked : DetailsUiEvent()
}