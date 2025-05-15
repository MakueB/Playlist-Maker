package com.makiev.playlistmaker.details.ui

import com.makiev.playlistmaker.createandeditplaylist.domain.models.Playlist
import com.makiev.playlistmaker.details.ui.models.PlaylistUiModel
import com.makiev.playlistmaker.utils.Utils

class PlaylistMapper {
    fun toUiModel(playlist: Playlist): PlaylistUiModel {
        val duration = Utils.getTotalDurationInMinutes(playlist.trackList)
        val formattedDuration = Utils.formatMinutesText(duration)
        val trackCount = playlist.trackList.size
        val wordForm = Utils.getTrackWordForm(trackCount)

        return PlaylistUiModel(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            imageUrl = playlist.imageUrl,
            totalDuration = formattedDuration,
            trackCount = trackCount,
            trackWordForm = wordForm,
            tracks = playlist.trackList
        )
    }
}