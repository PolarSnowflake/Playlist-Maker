package com.example.playlist_maker.data.search

import com.example.playlist_maker.domein.search.SearchHistoryInteractor
import com.example.playlist_maker.domein.player.Track

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun getSearchHistory(): List<Track> {
        return searchHistoryRepository.getSearchHistory()
    }

    override fun addTrackToHistory(track: Track) {
        searchHistoryRepository.addTrackToHistory(track)
    }

    override fun clearSearchHistory() {
        searchHistoryRepository.clearSearchHistory()
    }
}