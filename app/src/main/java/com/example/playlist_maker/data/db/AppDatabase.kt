package com.example.playlist_maker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteTrackEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTracksDao(): FavoriteTracksDao
}