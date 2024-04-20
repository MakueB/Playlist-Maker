package com.example.playlistmaker.search.data//package com.example.playlistmaker.data

import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun search(query: String): Resource<List<Track>> {
        val response = networkClient.sendRequest(TrackSearchRequest(query))
        return when (response.responseCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }
            200 -> {
                Resource.Success((response as TrackSearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.getTimeInMmSs(),
                        it.getCoverArtwork(),
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                })
            }
            else -> {
                Resource.Error("Ошибка сервера")
            }

        }
    }
}
