package com.example.playlistmaker

import com.example.playlistmaker.domain.models.Track

class ITunesResponse (
    val resulCount: Int,
    val results: List<Track>
)