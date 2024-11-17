package com.example.playlist_maker.domein.search

import com.example.playlist_maker.domein.player.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class SearchTracksInteractorImpl(
    private val repository: TrackRepository
) : SearchTracksInteractor {

    override fun searchTracks(query: String): Flow<Result<List<Track>>> {
        return repository.searchTracks(query)
            .catch { e -> emit(Result.failure(e)) }
    }
}