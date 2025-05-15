package com.makiev.playlistmaker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey @ColumnInfo("track_id")
    val trackId: Int,
    @ColumnInfo("playlist_id")
    val playlistId: Long,
    @ColumnInfo("track_name")
    val trackName: String,
    @ColumnInfo("artist_name")
    val artistName: String,
    @ColumnInfo("track_duration")
    val trackDuration: String,
    @ColumnInfo("image_url")
    val artworkUrl100: String,
    @ColumnInfo("collection_name")
    val collectionName: String,
    @ColumnInfo("release_date")
    val releaseDate: String?,
    @ColumnInfo("primary_genre_name")
    val primaryGenreName: String,
    @ColumnInfo("country")
    val country: String,
    @ColumnInfo("track_preview_url")
    val previewUrl: String,
    @ColumnInfo("added_timestamp")
    val addedTimestamp: Long
)
