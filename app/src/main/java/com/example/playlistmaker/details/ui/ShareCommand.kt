package com.example.playlistmaker.details.ui

sealed class ShareCommand {
    object ShowEmptyPlaylistMessage: ShareCommand()
    data class SharePlaylist(val text: String): ShareCommand()
}