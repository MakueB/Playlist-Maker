package com.example.playlistmaker.newplaylist.domain.models

import com.example.playlistmaker.search.domain.models.Track

data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val trackList: List<Track>
)
