package com.makiev.playlistmaker.library.ui.favorites

import com.makiev.playlistmaker.search.domain.models.Track


sealed interface FavoritesState {

    data object Loading : FavoritesState

    data class Content(
        val tracks: List<Track>
    ) : FavoritesState

    data class Empty(
        val message: String
    ) : FavoritesState
}