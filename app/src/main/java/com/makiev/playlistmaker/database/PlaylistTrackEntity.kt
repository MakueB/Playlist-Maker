package com.makiev.playlistmaker.database

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "playlist_tracks",
    primaryKeys = ["track_id", "playlist_id"]
)
data class PlaylistTrackEntity(
    @ColumnInfo(name = "track_id")
    val trackId: Int,
    @ColumnInfo(name = "playlist_id")
    val playlistId: Long,
    @ColumnInfo(name = "track_name")
    val trackName: String,
    @ColumnInfo(name = "artist_name")
    val artistName: String,
    @ColumnInfo(name = "track_duration")
    val trackDuration: String,
    @ColumnInfo(name = "image_url")
    val artworkUrl100: String,
    @ColumnInfo(name = "collection_name")
    val collectionName: String,
    @ColumnInfo(name = "release_date")
    val releaseDate: String?,
    @ColumnInfo(name = "primary_genre_name")
    val primaryGenreName: String,
    @ColumnInfo(name = "country")
    val country: String,
    @ColumnInfo(name = "track_preview_url")
    val previewUrl: String,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean,
    @ColumnInfo(name = "added_timestamp")
    val addedTimestamp: Long
)
