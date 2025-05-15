package com.makiev.playlistmaker.search.ui

import com.makiev.playlistmaker.search.domain.models.Track

interface TrackActionListener {
    fun onTrackClick(track: Track)
    fun onTrackLongClick(track: Track): Boolean
}
