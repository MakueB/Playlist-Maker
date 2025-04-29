package com.example.playlistmaker.playlistdetails.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.playlists.api.PlaylistsInteractor
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.CommonUtils
import kotlinx.coroutines.launch
import java.util.Locale

class PlaylistDetailsViewModel(private val interactor: PlaylistsInteractor) : ViewModel() {

    private val _shareCommand = MutableLiveData<ShareCommand>()
    val shareCommand: LiveData<ShareCommand> = _shareCommand

    fun onShareClicked(playlist: Playlist?) {
        val tracks = playlist?.trackList

        if (tracks.isNullOrEmpty()) {
            _shareCommand.value = ShareCommand.ShowEmptyPlaylistMessage
        } else {
            val text = buildShareText(playlist.name, playlist.description, tracks)
            _shareCommand.value = ShareCommand.SharePlaylist(text)
        }
    }

    private fun buildShareText(
        name: String,
        description: String,
        tracks: List<Track>
    ): String {
        val header = buildString {
            appendLine("Плейлист: $name")
            appendLine("Описание: $description")
            appendLine()
            appendLine("${tracks.size} ${CommonUtils.getTrackWordForm(tracks.size)}")
        }

        val trackLines = tracks.mapIndexed { index, track ->
            val duration = track.trackDuration.toDurationInSeconds()
            "${index + 1}. ${track.artistName} - ${track.trackName} ($duration)"
        }
        return header + "\n" + trackLines.joinToString("\n")
    }

    private fun String.toDurationInSeconds(): Int {
        val parts = this.split(":").map { it.toIntOrNull() ?: 0 }
        return when (parts.size) {
            2 -> parts[0] * 60 + parts[1] // ММ:СС
            3 -> parts[0] * 3600 + parts[1] * 60 + parts[2] // ЧЧ:ММ:СС
            else -> 0
        }
    }

    fun removeTrack(track: Track, playlistId: Long) {
        viewModelScope.launch {
            interactor.removeTrackFromPlaylist(track, playlistId)
        }
    }
}