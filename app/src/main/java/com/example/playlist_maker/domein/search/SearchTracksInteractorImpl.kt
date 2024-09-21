package com.example.playlist_maker.domein.search

import com.example.playlist_maker.domein.player.Track

class SearchTracksInteractorImpl(
    private val repository: TrackRepository
) : SearchTracksInteractor {
    override fun searchTracks(query: String, callback: (Result<List<Track>>) -> Unit) {
        repository.searchTracks(query) { result ->
            if (result.isSuccess) {
                callback(
                    Result.success(
                        result.getOrNull() ?: emptyList()
                    )
                ) // Возвращаем пустой список, если данных нет
            } else {
                callback(Result.failure(Exception()))
            }
        }
    }
}