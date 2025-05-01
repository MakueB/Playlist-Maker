package com.example.playlistmaker.playlistdetails.domain.api

import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.playlistdetails.ui.ShareCommand

interface PlaylistDetailsInteractor {
    fun execute(playlist: Playlist?): ShareCommand
}