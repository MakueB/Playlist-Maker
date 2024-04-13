package com.example.playlistmaker.data.search//package com.example.playlistmaker.data

import com.example.playlistmaker.data.search.dto.TrackSearchRequest
import com.example.playlistmaker.data.search.dto.TrackSearchResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track

//class TracksRepositoryImpl (private val networkClient: NetworkClient) : TracksRepository {
//    override fun search(query: String): List<Track> {
//        val response = networkClient.sendRequest(TrackSearchRequest(query))
//        if (response.responseCode == 200) {
//            return (response as TrackSearchResponse).results.map {
//                Track(it.trackId,
//                    it.trackName,
//                    it.artistName,
//                    it.getTimeInMmSs(),
//                    it.getCoverArtwork(),
//                    it.collectionName,
//                    it.releaseDate,
//                    it.primaryGenreName,
//                    it.country,
//                    it.previewUrl
//
//                ) }
//        } else {
//            return emptyList()
//        }
//    }
//}