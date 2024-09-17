package com.example.playlist_maker.data.search

import android.content.SharedPreferences
import com.example.playlist_maker.data.player.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepository(private val sharedPreferences: SharedPreferences) {

    fun getSearchHistory(): List<Track> {
        val json = sharedPreferences.getString("history", null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun saveSearchHistory(history: List<Track>) {
        val json = Gson().toJson(history)
        sharedPreferences.edit().putString("history", json).apply()
    }

    fun clearSearchHistory() {
        saveSearchHistory(emptyList())
    }

    fun addTrackToHistory(track: Track) {
        val history = getSearchHistory().toMutableList()
        history.remove(track)  // Удаляем трек, если он уже есть
        history.add(0, track)  // Добавляем трек в начало списка
        if (history.size > 10) {
            history.removeAt(history.size - 1)  // Ограничиваем историю 10 треками
        }
        saveSearchHistory(history)
    }
}