package com.example.playlistmaker.details.data

import com.example.playlistmaker.details.domain.api.DetailsRepository
import com.example.playlistmaker.details.ui.ShareCommand
import com.example.playlistmaker.details.ui.models.PlaylistUiModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.CommonUtils

class DetailsRepositoryImpl : DetailsRepository {

    override fun getShareCommand(playlistUiModel: PlaylistUiModel): ShareCommand {
        val tracks = playlistUiModel.tracks
        return if (tracks.isEmpty()) {
            ShareCommand.ShowEmptyPlaylistMessage
        } else {
            val text = buildShareText(
                playlistUiModel.name,
                playlistUiModel.description ?: "",
                tracks
            )
            ShareCommand.SharePlaylist(text)
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
            "${index + 1}. ${track.artistName} - ${track.trackName} (${duration})"
        }
        return header + "\n" + trackLines.joinToString("\n")
    }
}
