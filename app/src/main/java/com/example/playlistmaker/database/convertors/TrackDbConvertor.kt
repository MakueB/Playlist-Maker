package com.example.playlistmaker.database.convertors

import com.example.playlistmaker.database.PlaylistTrackEntity
import com.example.playlistmaker.database.TrackEntity
import com.example.playlistmaker.search.domain.models.Track

class TrackDbConvertor {
    fun map(trackEntity: TrackEntity): Track {
        return Track(
            trackEntity.trackId,
            trackEntity.playlistId,
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
            track.playlistId,
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

    // Конвертация PlaylistTrackEntity -> Track
    fun mapFromPlaylistTrackEntity(playlistTrackEntity: PlaylistTrackEntity): Track {
        return Track(
            playlistTrackEntity.trackId,
            playlistTrackEntity.playlistId,
            playlistTrackEntity.trackName,
            playlistTrackEntity.artistName,
            playlistTrackEntity.trackDuration,
            playlistTrackEntity.artworkUrl100,
            playlistTrackEntity.collectionName,
            playlistTrackEntity.releaseDate,
            playlistTrackEntity.primaryGenreName,
            playlistTrackEntity.country,
            playlistTrackEntity.previewUrl,
            playlistTrackEntity.isFavorite // Признак, что трек не обязательно избранный
        )
    }

    // Конвертация Track -> PlaylistTrackEntity
    fun mapToPlaylistTrackEntity(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            track.trackId,
            track.playlistId,
            track.trackName,
            track.artistName,
            track.trackDuration,
            track.artworkUrl100,
            track.collectionName ?: "",
            track.releaseDate,
            track.primaryGenreName ?: "",
            track.country ?: "",
            track.previewUrl ?: "",
            track.isFavorite,
            addedTimestamp = System.currentTimeMillis()
        )
    }
}