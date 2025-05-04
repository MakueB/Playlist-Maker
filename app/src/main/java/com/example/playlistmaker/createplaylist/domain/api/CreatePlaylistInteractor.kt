package com.example.playlistmaker.createplaylist.domain.api

import com.example.playlistmaker.createplaylist.domain.models.Playlist

interface CreatePlaylistInteractor {
    suspend fun savePlaylist(playlist: Playlist)
}