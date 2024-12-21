package com.example.playlist_maker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlist_maker.data.db.favoriteDB.FavoriteTrackEntity
import com.example.playlist_maker.data.db.favoriteDB.FavoriteTracksDao
import com.example.playlist_maker.data.db.playlistsDB.PlaylistDao
import com.example.playlist_maker.data.db.playlistsDB.PlaylistEntity

@Database(entities = [FavoriteTrackEntity::class, PlaylistEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTracksDao(): FavoriteTracksDao
    abstract fun playlistDao(): PlaylistDao
}