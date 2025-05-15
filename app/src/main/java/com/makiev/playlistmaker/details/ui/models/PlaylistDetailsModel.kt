package com.makiev.playlistmaker.details.ui.models

import com.makiev.playlistmaker.search.domain.models.Track


data class PlaylistUiModel(
    val id: Long,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val totalDuration: String,
    val trackCount: Int,
    val trackWordForm: String,
    val tracks: List<Track>
)