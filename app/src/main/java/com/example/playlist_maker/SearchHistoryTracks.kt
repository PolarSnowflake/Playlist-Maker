package com.example.playlist_maker

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryTracks(
    private val context: Context,
    private val historyRecyclerView: RecyclerView,
    private val clearHistoryButton: MaterialButton,
    private val historyHeader: View
) {

    private val sharedPreferences =
        context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val historyAdapter: TrackAdapter

    init {
        historyAdapter = TrackAdapter(emptyList()) { track ->
            addTrackToHistory(track)
        }
        historyRecyclerView.adapter = historyAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(context)

        clearHistoryButton.setOnClickListener {
            clearHistory()
        }

        loadSearchHistory()
    }

    // Добавление трека в историю поиска
    fun addTrackToHistory(track: Track) {
        val history = getSearchHistory().toMutableList()
        history.remove(track) // Удаляем трек, если он уже есть в истории
        history.add(0, track) // Добавляем трек в начало списка

        if (history.size > 10) {
            history.removeAt(history.size - 1) // Ограничиваем размер истории 10 треками
        }

        saveSearchHistory(history)
        loadSearchHistory()
    }

    // Получение истории поиска
    private fun getSearchHistory(): List<Track> {
        val json = sharedPreferences.getString("history", null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    // Сохранение истории поиска
    private fun saveSearchHistory(history: List<Track>) {
        val json = Gson().toJson(history)
        sharedPreferences.edit().putString("history", json).apply()
    }

    // Загрузка и отображение истории поиска
    fun loadSearchHistory() {
        if (hasHistory()) {
            val history = getSearchHistory()
            historyAdapter.updateTracks(history)
            historyRecyclerView.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
            historyHeader.visibility = View.VISIBLE
        } else {
            hideHistory()
        }
    }

    // Проверка наличия истории поиска
    fun hasHistory(): Boolean {
        return !getSearchHistory().isEmpty()
    }

    // Скрытие элементов UI
    fun hideHistory() {
        historyRecyclerView.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        historyHeader.visibility = View.GONE
    }

    // Очистка истории поиска
    private fun clearHistory() {
        saveSearchHistory(emptyList())
        hideHistory()
    }
}