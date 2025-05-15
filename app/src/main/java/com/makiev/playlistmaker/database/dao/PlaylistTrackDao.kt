package com.makiev.playlistmaker.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.makiev.playlistmaker.database.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackToPlaylist(track: PlaylistTrackEntity): Long

    @Query("SELECT * FROM playlist_tracks WHERE playlist_id = :playlistId ORDER BY added_timestamp DESC")
    suspend fun getTracksByPlaylist(playlistId: Long): List<PlaylistTrackEntity>

    @Query("SELECT * FROM playlist_tracks WHERE track_id = :trackId AND playlist_id = :playlistId")
    suspend fun getTrackInPlaylist(trackId: Int, playlistId: Long): PlaylistTrackEntity?

    @Query("SELECT * FROM playlist_tracks WHERE track_id = :trackId")
    suspend fun getPlaylistsForTrack(trackId: Int): List<PlaylistTrackEntity>

    @Query("DELETE FROM playlist_tracks WHERE track_id = :trackId")
    suspend fun deleteTrackById(trackId: Int)

    @Delete
    suspend fun removeTrackFromPlaylist(track: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_tracks WHERE playlist_id = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)
}