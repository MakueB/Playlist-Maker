package com.example.playlistmaker.data.search.dto

class TrackSearchResponse (
    val resulCount: Int,
    val results: List<TrackDto>
) : Response()