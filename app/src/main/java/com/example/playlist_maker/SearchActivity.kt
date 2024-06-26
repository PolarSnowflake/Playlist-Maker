package com.example.playlist_maker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var searchBar: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var iTunesService: iTunesAPI
    private lateinit var placeholderError: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var errorPlaceholderLayout: LinearLayout
    private lateinit var updateButtonLayout: LinearLayout
    private lateinit var updateButton: Button
    private lateinit var searchHistoryTracks: SearchHistoryTracks
    private lateinit var searchHint: TextView
    private var lastQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        iTunesService = retrofit.create(iTunesAPI::class.java)

        searchBar = findViewById(R.id.searchBar)
        clearButton = findViewById(R.id.clear_button)
        recyclerView = findViewById(R.id.recyclerView)
        placeholderError = findViewById(R.id.placeholderError)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        errorPlaceholderLayout = findViewById(R.id.error_placeholder_layout)
        updateButtonLayout = findViewById(R.id.update_button_layout)
        updateButton = findViewById(R.id.update_button)
        searchHint = findViewById(R.id.searchHint)

        recyclerView.visibility = View.GONE
        errorPlaceholderLayout.visibility = View.GONE

        trackAdapter = TrackAdapter(emptyList()) { track ->  // Начинаем с пустого списка треков
            searchHistoryTracks.addTrackToHistory(track)
            searchHistoryTracks.hideHistory()
            startPlayerActivity(track)
        }

        recyclerView.adapter = trackAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchHistoryTracks = SearchHistoryTracks(
            this,
            findViewById(R.id.recyclerHistoryTracks),
            findViewById(R.id.historyClear),
            findViewById(R.id.searchHistoryHeader)
        )

        // Слушатель для истории поиска
        searchHistoryTracks.setOnItemClickListener { track ->
            startPlayerActivity(track)
        }

        // Настройка событий для поиска
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (s.isNullOrEmpty()) {
                    recyclerView.visibility = View.GONE
                    trackAdapter.updateTracks(emptyList())
                    hideErrorPlaceholder()
                    hideUpdateButton()
                    searchHint.visibility = View.VISIBLE // Показываем searchHint при пустом тексте
                    if (searchBar.hasFocus()) {
                        searchHistoryTracks.loadSearchHistory() // Показываем историю, если есть
                    }
                } else {
                    searchHistoryTracks.hideHistory() // Скрываем историю при вводе текста
                    searchHint.visibility = View.GONE // Скрываем searchHint при вводе текста
                    performSearch(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    // Дополнительная проверка в afterTextChanged для обработки случая длительного нажатия кнопки "Стереть"
                    recyclerView.visibility = View.GONE
                    trackAdapter.updateTracks(emptyList())
                    hideErrorPlaceholder()
                    hideUpdateButton()
                    if (searchBar.hasFocus()) {
                        searchHistoryTracks.loadSearchHistory()
                    }
                }
            }
        })

        // Отслеживание состояния фокуса
        searchBar.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchBar.text.isEmpty()) {
                searchHint.visibility = View.VISIBLE
                searchHistoryTracks.loadSearchHistory()
            } else {
                searchHint.visibility = View.GONE
                searchHistoryTracks.hideHistory()
            }
        }

        // Кнопка "Готово" на клавиатуре
        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(searchBar.text.toString())
                hideKeyboard()
                searchHistoryTracks.hideHistory()
                true
            } else {
                false
            }
        }

        // Кнопка "Очистить поисковый запрос"
        clearButton.setOnClickListener {
            searchBar.text.clear()
            hideKeyboard()
            recyclerView.visibility = View.GONE
            trackAdapter.updateTracks(emptyList())
            hideErrorPlaceholder()
            hideUpdateButton()
            searchHint.visibility = View.VISIBLE // Показываем searchHint после очистки текста
            searchHistoryTracks.loadSearchHistory() // Загрузка истории после очистки текста
        }

        // Кнопка "Обновить"
        updateButton.setOnClickListener {
            retryLastSearch()
        }

        // Кнопка "Назад"
        val backButton: Button = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            finish()
        }

        // Восстановление состояния при создании активити
        if (savedInstanceState != null) {
            lastQuery = savedInstanceState.getString("last_query", "")
            searchBar.setText(lastQuery)
            searchBar.setSelection(lastQuery.length)
            if (searchBar.hasFocus()) {
                if (lastQuery.isEmpty()) {
                    if (searchHistoryTracks.hasHistory()) {
                        recyclerView.visibility = View.GONE
                        hideErrorPlaceholder()
                        hideUpdateButton()
                        searchHint.visibility = View.GONE
                        searchHistoryTracks.loadSearchHistory()
                    } else {
                        recyclerView.visibility = View.GONE
                        hideErrorPlaceholder()
                        hideUpdateButton()
                        searchHint.visibility = View.GONE
                        searchHistoryTracks.hideHistory()
                    }
                } else {
                    performSearch(lastQuery)
                    searchHint.visibility = View.GONE
                    searchHistoryTracks.hideHistory()
                }
            } else {
                searchHint.visibility = if (lastQuery.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        if (searchBar.text.isEmpty() && !searchBar.hasFocus()) {
            searchHistoryTracks.hideHistory()
        }

        // Проверка наличия трека в SharedPreferences
        val sharedPreferences = getSharedPreferences("player_prefs", MODE_PRIVATE)
        val trackJson = sharedPreferences.getString("current_track", null)
        if (trackJson != null) {
            val track = Gson().fromJson(trackJson, Track::class.java)
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
        }
    }

    // Сохр текущего трека в SharedPreferences при паузе активности
    override fun onPause() {
        super.onPause()
        val sharedPreferences = getSharedPreferences("player_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val track = intent.getSerializableExtra("track") as? Track
        if (track != null) {
            val trackJson = Gson().toJson(track)
            editor.putString("current_track", trackJson)
        } else {
            editor.remove("current_track")
        }
        editor.apply()
    }

    // Сохранение состояния
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_TEXT", searchBar.text.toString())
    }

    // Восстановление состояния
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val searchText = savedInstanceState.getString("SEARCH_TEXT")
        searchBar.setText(searchText)
        searchBar.setSelection(searchText?.length ?: 0)
        searchHint.visibility = if (searchText.isNullOrEmpty()) View.VISIBLE else View.GONE
    }

    // Выполнение поискового запроса
    private fun performSearch(query: String) {
        lastQuery = query
        searchHistoryTracks.hideHistory()
        val call = iTunesService.search(query)
        call.enqueue(object : Callback<ItunesSearchResponse> {
            override fun onResponse(
                call: Call<ItunesSearchResponse>,
                response: Response<ItunesSearchResponse>
            ) {
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    if (searchResponse != null && searchResponse.results.isNotEmpty()) {
                        val tracks = searchResponse.results
                        trackAdapter.updateTracks(tracks)
                        recyclerView.visibility = View.VISIBLE
                        hideErrorPlaceholder()
                        hideUpdateButton()
                    } else {
                        showNoResultsPlaceholder()
                    }
                } else {
                    showErrorPlaceholder()
                }
            }

            override fun onFailure(call: Call<ItunesSearchResponse>, t: Throwable) {
                showErrorPlaceholder()
            }
        })
    }

    // Повтор последнего неудавшегося запроса
    private fun retryLastSearch() {
        performSearch(lastQuery)
    }

    // Плейсхолдер "Ничего не найдено"
    private fun showNoResultsPlaceholder() {
        trackAdapter.updateTracks(emptyList())
        recyclerView.visibility = View.GONE
        errorPlaceholderLayout.visibility = View.VISIBLE
        placeholderError.visibility = View.VISIBLE
        placeholderMessage.visibility = View.VISIBLE
        updateButtonLayout.visibility = View.GONE
        placeholderMessage.text = getString(R.string.nothing_found)
        placeholderError.setImageResource(R.drawable.not_found_icon)
        searchHistoryTracks.hideHistory()
    }

    // Плейсхолдер "Ошибка подключения"
    private fun showErrorPlaceholder() {
        trackAdapter.updateTracks(emptyList())
        recyclerView.visibility = View.GONE
        errorPlaceholderLayout.visibility = View.VISIBLE
        placeholderError.visibility = View.VISIBLE
        placeholderMessage.visibility = View.VISIBLE
        updateButtonLayout.visibility = View.VISIBLE
        placeholderMessage.text = getString(R.string.no_int)
        placeholderError.setImageResource(R.drawable.no_int_icon)
    }

    // Скрытие плейсхолдера ошибки
    private fun hideErrorPlaceholder() {
        errorPlaceholderLayout.visibility = View.GONE
    }

    // Скрытие кнопки "Обновить"
    private fun hideUpdateButton() {
        updateButtonLayout.visibility = View.GONE
    }

    // Скрытие клавиатуры
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchBar.windowToken, 0)
    }

    // Переход на PlayerActivity
    private fun startPlayerActivity(track: Track) {
        val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
        intent.putExtra("track", track)
        startActivity(intent)
    }
}