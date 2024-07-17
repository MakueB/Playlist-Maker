package com.example.playlistmaker.library.ui.favorites

import com.example.playlistmaker.newplaylist.domain.models.Playlist

sealed interface PlaylistsState {
    data object Loading: PlaylistsState
    data class Content(val playlists: List<Playlist>) : PlaylistsState
    data class Empty(val message: String) : PlaylistsState
}