package com.example.playlistmaker

import com.example.playlistmaker.domain.models.Track

fun interface OnTrackClickListener {
    fun onTrackClick(track: Track)
}