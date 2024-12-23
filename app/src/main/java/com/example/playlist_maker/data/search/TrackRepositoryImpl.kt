package com.example.playlist_maker.data.search

import com.example.playlist_maker.domein.player.Track
import com.example.playlist_maker.domein.search.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class TrackRepositoryImpl(private val api: ITunesAPI) : TrackRepository {
    override fun searchTracks(query: String): Flow<Result<List<Track>>> = flow {
        try {
            val response = api.search(query)
            if (response.resultCount > 0 && response.results.isNotEmpty()) {
                val tracks = response.results.map { it.toDomain() }
                emit(Result.success(tracks))
            } else {
                emit(Result.success(emptyList()))
            }
        } catch (e: IOException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}