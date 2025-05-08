package com.example.playlistmaker.details.ui

import com.example.playlistmaker.createandeditplaylist.domain.models.Playlist
import com.example.playlistmaker.details.ui.models.PlaylistUiModel
import com.example.playlistmaker.utils.CommonUtils

class PlaylistMapper {
    fun toUiModel(playlist: Playlist): PlaylistUiModel {
        val duration = CommonUtils.getTotalDurationInMinutes(playlist.trackList)
        val formattedDuration = CommonUtils.formatMinutesText(duration)
        val trackCount = playlist.trackList.size
        val wordForm = CommonUtils.getTrackWordForm(trackCount)

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