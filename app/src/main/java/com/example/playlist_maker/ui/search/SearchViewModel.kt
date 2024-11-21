package com.example.playlist_maker.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.domein.player.Track
import com.example.playlist_maker.domein.search.SearchHistoryInteractor
import com.example.playlist_maker.domein.search.SearchTracksInteractor
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksInteractor: SearchTracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _searchResults = MutableLiveData<List<Track>>()
    val searchResults: LiveData<List<Track>> get() = _searchResults

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> get() = _error

    private val _searchHistory = MutableLiveData<List<Track>>()
    val searchHistory: LiveData<List<Track>> get() = _searchHistory

    private var lastQuery: String = ""

    init {
        loadSearchHistory()
    }

    fun searchTracks(query: String) {
        lastQuery = query
        _loading.value = true
        viewModelScope.launch {
            searchTracksInteractor.searchTracks(query).collect { result ->
                _loading.postValue(false)
                result.onSuccess { tracks ->
                    _searchResults.postValue(tracks)
                }.onFailure {
                    _searchResults.postValue(emptyList())
                    _error.postValue(true)
                }
            }
        }
    }

    fun loadSearchHistory() {
        _searchHistory.value = searchHistoryInteractor.getSearchHistory()
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrackToHistory(track)
        loadSearchHistory()
    }

    fun clearSearchHistory() {
        searchHistoryInteractor.clearSearchHistory()
        loadSearchHistory()
    }
}