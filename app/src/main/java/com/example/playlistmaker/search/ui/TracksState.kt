package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

sealed interface TracksState {
    data object Loading: TracksState

    data class Content(
        val tracks: List<Track>
    ): TracksState

    data class History(
        val tracks: List<Track>
    ): TracksState

    data class Error(
        val errorMessage: String
    ): TracksState

    data class Empty(
        val message: String
    ): TracksState
}