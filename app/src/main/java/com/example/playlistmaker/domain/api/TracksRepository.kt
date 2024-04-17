package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.utils.Resource

interface TracksRepository {
    fun search (query: String) : Resource<List<Track>>
}