package com.example.playlistmaker.database.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmaker.newplaylist.domain.models.Playlist

interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlist: Playlist): Long
}