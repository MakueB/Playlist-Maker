package com.example.playlistmaker.details.domain.api

import com.example.playlistmaker.createplaylist.domain.models.Playlist
import com.example.playlistmaker.details.ui.ShareCommand

interface DetailsInteractor {
    fun execute(playlist: Playlist?): ShareCommand
}