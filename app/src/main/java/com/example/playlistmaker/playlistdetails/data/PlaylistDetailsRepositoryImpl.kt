package com.example.playlistmaker.playlistdetails.data

import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.playlistdetails.domain.api.PlaylistDetailsRepository
import com.example.playlistmaker.playlistdetails.ui.ShareCommand
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.CommonUtils

class PlaylistDetailsRepositoryImpl: PlaylistDetailsRepository {
    override fun execute(playlist: Playlist?): ShareCommand {
        val tracks = playlist?.trackList ?: emptyList()
        return if (tracks.isEmpty()) {
            ShareCommand.ShowEmptyPlaylistMessage
        } else {
            val text = buildShareText(playlist?.name ?: "", playlist?.description ?: "", tracks)
            return ShareCommand.SharePlaylist(text)
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
            val duration = CommonUtils.toDurationInSeconds(track.trackDuration)
            "${index + 1}. ${track.artistName} - ${track.trackName} ($duration)"
        }
        return header + "\n" + trackLines.joinToString("\n")
    }
}