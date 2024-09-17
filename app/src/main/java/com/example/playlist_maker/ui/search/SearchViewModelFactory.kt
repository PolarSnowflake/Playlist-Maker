package com.example.playlist_maker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.domein.search.SearchTracksInteractor
import com.example.playlist_maker.data.search.SearchHistoryRepository

class SearchViewModelFactory(
    private val searchTracksInteractor: SearchTracksInteractor,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchTracksInteractor, searchHistoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}