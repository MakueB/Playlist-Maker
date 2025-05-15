package com.makiev.playlistmaker.details.data

import com.makiev.playlistmaker.details.domain.api.DetailsRepository
import com.makiev.playlistmaker.details.ui.ShareCommand
import com.makiev.playlistmaker.details.ui.models.PlaylistUiModel
import com.makiev.playlistmaker.search.domain.models.Track
import com.makiev.playlistmaker.utils.Utils

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
            appendLine("${tracks.size} ${Utils.getTrackWordForm(tracks.size)}")
        }

        val trackLines = tracks.mapIndexed { index, track ->
            val duration = Utils.toDurationInSeconds(track.trackDuration)
            "${index + 1}. ${track.artistName} - ${track.trackName} (${duration})"
        }
        return header + "\n" + trackLines.joinToString("\n")
    }
}
