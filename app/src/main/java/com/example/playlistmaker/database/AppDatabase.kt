package com.example.playlistmaker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.database.dao.PlaylistDao
import com.example.playlistmaker.database.dao.TrackDao

@Database(version = 2, entities = [TrackEntity::class, PlaylistEntity::class])
abstract class AppDatabase: RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
}