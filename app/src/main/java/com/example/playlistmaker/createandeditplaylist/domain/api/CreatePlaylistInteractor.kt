package com.example.playlistmaker.createandeditplaylist.domain.api

import com.example.playlistmaker.createandeditplaylist.domain.models.Playlist

interface CreatePlaylistInteractor {
    suspend fun savePlaylist(playlist: Playlist)
}