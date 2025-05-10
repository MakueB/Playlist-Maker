package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

interface TrackActionListener {
    fun onTrackClick(track: Track)
    fun onTrackLongClick(track: Track): Boolean
}
