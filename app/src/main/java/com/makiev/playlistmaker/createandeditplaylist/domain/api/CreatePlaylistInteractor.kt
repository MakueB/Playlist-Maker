package com.makiev.playlistmaker.createandeditplaylist.domain.api

import com.makiev.playlistmaker.createandeditplaylist.domain.models.Playlist

interface CreatePlaylistInteractor {
    suspend fun savePlaylist(playlist: Playlist)
}