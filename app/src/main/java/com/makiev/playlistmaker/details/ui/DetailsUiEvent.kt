package com.makiev.playlistmaker.details.ui

import com.makiev.playlistmaker.search.domain.models.Track

sealed class DetailsUiEvent {
    data class LoadPlaylist(val id: Long) : DetailsUiEvent()
    data class RemoveTrack(val track: Track) : DetailsUiEvent()
    object DeletePlaylist : DetailsUiEvent()
    object ShareClicked : DetailsUiEvent()
}