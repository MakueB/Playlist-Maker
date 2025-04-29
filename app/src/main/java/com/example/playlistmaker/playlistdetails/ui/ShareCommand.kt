package com.example.playlistmaker.playlistdetails.ui

import org.w3c.dom.Text

sealed class ShareCommand {
    object ShowEmptyPlaylistMessage: ShareCommand()
    data class SharePlaylist(val text: String): ShareCommand()
}