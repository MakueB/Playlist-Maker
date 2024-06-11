package com.example.playlistmaker.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.database.TrackEntity

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListOfTracks(tracks: List<TrackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("SELECT * FROM tracks ORDER BY added_timestamp DESC")
    suspend fun getTracksAll(): List<TrackEntity>

    @Query("SELECT track_id FROM tracks")
    suspend fun getIdAll(): List<Int>

    @Query("SELECT * FROM tracks WHERE track_id LIKE :trackId")
    suspend fun getTrack(trackId: Int): TrackEntity

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("DELETE FROM tracks")
    suspend fun deleteAll()
}