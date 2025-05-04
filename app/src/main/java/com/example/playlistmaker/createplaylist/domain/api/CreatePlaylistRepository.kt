package com.example.playlistmaker.createplaylist.domain.api

import com.example.playlistmaker.createplaylist.domain.models.Playlist

interface CreatePlaylistRepository {
    suspend fun savePlaylist(playlist: Playlist): Long
}