package com.makiev.playlistmaker.createandeditplaylist.domain.api

import com.makiev.playlistmaker.createandeditplaylist.domain.models.Playlist

interface CreatePlaylistRepository {
    suspend fun savePlaylist(playlist: Playlist): Long
}