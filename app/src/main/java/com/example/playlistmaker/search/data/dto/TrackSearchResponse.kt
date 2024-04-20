package com.example.playlistmaker.search.data.dto

class TrackSearchResponse (
    val resulCount: Int,
    val results: List<TrackDto>
) : Response()