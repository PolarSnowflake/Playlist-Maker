package com.example.playlist_maker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTracksDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrack(trackEntity: FavoriteTrackEntity)

    @Query("DELETE FROM favorite_tracks WHERE id = :id")
    suspend fun delTrack(id: Long)

    @Query("SELECT * FROM favorite_tracks ORDER BY addedTimestamp DESC")
    fun getFavTracks(): Flow<List<FavoriteTrackEntity>>

    @Query("SELECT id FROM favorite_tracks")
    fun getFavIds(): Flow<List<Long>>
}