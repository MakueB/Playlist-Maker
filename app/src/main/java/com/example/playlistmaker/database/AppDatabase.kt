package com.example.playlistmaker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.playlistmaker.database.dao.PlaylistDao
import com.example.playlistmaker.database.dao.TrackDao

@Database(version = 2, entities = [TrackEntity::class, PlaylistEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `tracks` (
                `track_id` INTEGER PRIMARY KEY NOT NULL,
                `playlist_id` INTEGER NOT NULL,
                `track_name` TEXT NOT NULL,
                `artist_name` TEXT NOT NULL,
                `track_duration` TEXT NOT NULL,
                `image_url` TEXT NOT NULL,
                `collection_name` TEXT NOT NULL,
                `release_date` TEXT,
                `primary_genre_name` TEXT NOT NULL,
                `country` TEXT NOT NULL,
                `track_preview_url` TEXT NOT NULL,
                `added_timestamp` INTEGER NOT NULL
            )
        """.trimIndent()
            )
        }
    }
}
