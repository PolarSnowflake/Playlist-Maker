package com.example.playlist_maker.ui.search

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker.domein.player.Track
import com.google.android.material.button.MaterialButton


class SearchHistoryTracks(
    private val context: Context,
    private val historyRecyclerView: RecyclerView,
    private val clearHistoryButton: MaterialButton,
    private val historyHeader: View,
    private val viewModel: SearchViewModel
) {

    // Адаптер для треков в истории поиска
    private val historyAdapter: TrackAdapter = TrackAdapter(emptyList()) { track ->
        // При клике на трек добавляем его в историю и запускаем плеер
        viewModel.addTrackToHistory(track)
        startPlayerFragment(track)
    }

    init {
        historyRecyclerView.adapter = historyAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(context)

        // Очистка истории поиска
        clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        viewModel.loadSearchHistory()
    }

    // Загрузка и отображение истории поиска
    fun loadSearchHistory() {
        viewModel.searchHistory.observe(context as LifecycleOwner, { history ->
            if (history.isNotEmpty()) {
                historyAdapter.updateTracks(history)
                historyRecyclerView.visibility = View.VISIBLE
                clearHistoryButton.visibility = View.VISIBLE
                historyHeader.visibility = View.VISIBLE
            } else {
                hideHistory()
            }
        })
    }

    //Скрытие истории поиска
    fun hideHistory() {
        historyRecyclerView.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        historyHeader.visibility = View.GONE
    }

    // Переход на PlayerFragment при выборе трека из истории
    private fun startPlayerFragment(track: Track) {
        val navController = historyRecyclerView.findNavController()
        val action = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(track)
        navController.navigate(action)
    }
}