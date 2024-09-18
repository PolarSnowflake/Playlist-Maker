package com.example.playlist_maker.domein.search

import com.example.playlist_maker.domein.player.Track

interface SearchHistoryInteractor {
    fun getSearchHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearSearchHistory()
}