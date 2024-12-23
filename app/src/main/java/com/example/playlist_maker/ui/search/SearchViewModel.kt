package com.example.playlist_maker.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.domein.player.Track
import com.example.playlist_maker.domein.search.SearchHistoryInteractor
import com.example.playlist_maker.domein.search.SearchTracksInteractor
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

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
                    if (tracks.isEmpty()) {
                        _searchResults.postValue(emptyList())
                        _error.postValue(false)
                    } else {
                        _searchResults.postValue(tracks)
                        _error.postValue(false)
                    }
                }.onFailure { exception ->
                    if (exception is IOException) {
                        _searchResults.postValue(emptyList())
                        _error.postValue(true)
                    } else if (exception is HttpException && exception.code() == 404) {
                        _searchResults.postValue(emptyList())
                        _error.postValue(false)
                    } else {
                        _searchResults.postValue(emptyList())
                        _error.postValue(true)
                    }
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