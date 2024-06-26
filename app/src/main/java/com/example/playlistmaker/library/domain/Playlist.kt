package com.example.playlistmaker.library.domain

import com.example.playlistmaker.search.domain.models.Track

data class Playlist(private val playlistId: Int,
                    private val trackList: List<Track>)
