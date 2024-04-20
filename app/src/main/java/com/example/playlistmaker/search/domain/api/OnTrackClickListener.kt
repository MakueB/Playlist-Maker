package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

fun interface OnTrackClickListener {
    fun onTrackClick(track: Track)
}