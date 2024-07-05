package com.example.playlistmaker.library.ui.favorites

import com.example.playlistmaker.search.domain.models.Track


sealed interface FavoritesState {

    data object Loading : FavoritesState

    data class Content(
        val tracks: List<Track>
    ) : FavoritesState

    data class Empty(
        val message: String
    ) : FavoritesState
}