package com.makiev.playlistmaker.search.domain.api

import com.makiev.playlistmaker.search.domain.models.Track
import com.makiev.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun search (query: String) : Flow<Resource<List<Track>>>
}