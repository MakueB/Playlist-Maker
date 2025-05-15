package com.makiev.playlistmaker.database.convertors

import com.makiev.playlistmaker.database.PlaylistTrackEntity
import com.makiev.playlistmaker.database.TrackEntity
import com.makiev.playlistmaker.search.domain.models.Track

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

    fun map(track: Track, playlistId: Long): TrackEntity {
        return TrackEntity(
            track.trackId,
            playlistId,
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
    fun mapToPlaylistTrackEntity(track: Track, playlistId: Long): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            track.trackId,
            playlistId,
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