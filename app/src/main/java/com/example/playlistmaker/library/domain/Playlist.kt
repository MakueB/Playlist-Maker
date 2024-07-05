package com.example.playlistmaker.library.domain

import com.example.playlistmaker.search.domain.models.Track

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val trackList: List<Track>
)
