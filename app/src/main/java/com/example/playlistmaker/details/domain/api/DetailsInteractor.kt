package com.example.playlistmaker.details.domain.api

import com.example.playlistmaker.createandeditplaylist.domain.models.Playlist
import com.example.playlistmaker.details.ui.ShareCommand

interface DetailsInteractor {
    fun execute(playlist: Playlist?): ShareCommand
}