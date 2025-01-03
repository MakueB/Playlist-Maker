package com.example.playlistmaker.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.database.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackToPlaylist(track: PlaylistTrackEntity): Long

    @Query("SELECT * FROM playlist_tracks WHERE playlist_id = :playlistId")
    suspend fun getTracksByPlaylist(playlistId: Long): List<PlaylistTrackEntity>

    @Query("SELECT * FROM playlist_tracks WHERE track_id = :trackId AND playlist_id = :playlistId")
    suspend fun getTrackInPlaylist(trackId: Int, playlistId: Long): PlaylistTrackEntity?

    @Delete
    suspend fun removeTrackFromPlaylist(track: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_tracks WHERE playlist_id = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)
}