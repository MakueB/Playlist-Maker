package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

fun interface OnTrackClickListener {
    fun onTrackClick(track: Track)
}