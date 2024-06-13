package com.example.playlistmaker.database.dao

import com.example.playlistmaker.database.TrackEntity
import com.example.playlistmaker.search.domain.models.Track

class TrackDbConvertor {
    fun map(trackEntity: TrackEntity): Track {
        return Track(
            trackEntity.trackId,
            trackEntity.trackName,
            trackEntity.artistName,
            trackEntity.trackDuration,
            trackEntity.artworkUrl100,
            trackEntity.collectionName,
            trackEntity.releaseDate,
            trackEntity.primaryGenreName,
            trackEntity.country,
            trackEntity.previewUrl,
            true
        )
    }

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackDuration,
            track.artworkUrl100,
            track.collectionName!!,
            track.releaseDate,
            track.primaryGenreName!!,
            track.country!!,
            track.previewUrl!!,
            addedTimestamp = System.currentTimeMillis()
        )
    }
}