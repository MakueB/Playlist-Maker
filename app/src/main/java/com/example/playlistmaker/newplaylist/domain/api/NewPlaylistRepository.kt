package com.example.playlistmaker.newplaylist.domain.api

import com.example.playlistmaker.newplaylist.domain.models.Playlist

interface NewPlaylistRepository {
    suspend fun savePlaylist(playlist: Playlist): Long
}