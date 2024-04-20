package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource

interface TracksRepository {
    fun search (query: String) : Resource<List<Track>>
}