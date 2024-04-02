package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.models.Track

class TrackSearchResponse (
    val resulCount: Int,
    val results: List<TrackDto>
) : Response()