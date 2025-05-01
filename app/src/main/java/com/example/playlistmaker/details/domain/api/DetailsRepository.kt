package com.example.playlistmaker.details.domain.api

import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.details.ui.ShareCommand

interface DetailsRepository {
    fun execute(playlist: Playlist?): ShareCommand
}