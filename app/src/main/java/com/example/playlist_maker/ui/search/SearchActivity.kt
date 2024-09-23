package com.example.playlist_maker.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.playlist_maker.R
import com.example.playlist_maker.domein.player.Track
import com.example.playlist_maker.ui.player.PlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var searchBar: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchHint: TextView
    private lateinit var progressBarScreen: LinearLayout
    private lateinit var errorPlaceholderLayout: LinearLayout
    private lateinit var placeholderError: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var updateButtonLayout: LinearLayout
    private lateinit var updateButton: Button
    private lateinit var searchHistoryTracks: SearchHistoryTracks
    private val viewModel: SearchViewModel by viewModel()

    private var lastQuery: String = ""
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchBar = findViewById(R.id.searchBar)
        clearButton = findViewById(R.id.clear_button)
        recyclerView = findViewById(R.id.recyclerView)
        searchHint = findViewById(R.id.searchHint)
        progressBarScreen = findViewById(R.id.progressBarLayout)
        errorPlaceholderLayout = findViewById(R.id.error_placeholder_layout)
        placeholderError = findViewById(R.id.placeholderError)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        updateButtonLayout = findViewById(R.id.update_button_layout)
        updateButton = findViewById(R.id.update_button)

        trackAdapter = TrackAdapter(emptyList()) { track ->  // Начинаем с пустого списка треков
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                viewModel.addTrackToHistory(track)
                searchHistoryTracks.hideHistory()
                startPlayerActivity(track)
            }, 300)
        }

        recyclerView.adapter = trackAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchHistoryTracks = SearchHistoryTracks(
            this,
            findViewById(R.id.recyclerHistoryTracks),
            findViewById(R.id.historyClear),
            findViewById(R.id.searchHistoryHeader),
            viewModel
        )

        setupObservers()

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
                    progressBarScreen.visibility = View.VISIBLE
                    debounceSearch(s.toString())
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

        // Отслеживание фокуса
        searchBar.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchBar.text.isEmpty()) {
                searchHint.visibility = View.VISIBLE
                searchHistoryTracks.loadSearchHistory()
                recyclerView.visibility = View.GONE
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
    }

    private fun setupObservers() {
        // Изменение списка треков
        viewModel.searchResults.observe(this) { tracks ->
            progressBarScreen.visibility = View.GONE
            if (tracks.isNotEmpty()) {
                trackAdapter.updateTracks(tracks)  // Обновляем треки
                recyclerView.visibility = View.VISIBLE
            } else {
                showNoResultsPlaceholder()  // Показываем плейсхолдер, если результаты пусты
            }
        }

        // Изменение состояния загрузки
        viewModel.loading.observe(this) { isLoading ->
            progressBarScreen.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Ошибка
        viewModel.error.observe(this) { isError ->
            if (isError) showErrorPlaceholder()  // Показываем плейсхолдер ошибки
        }
    }
    // Выполнение поискового запроса с задержкой (debounce)
    private fun debounceSearch(query: String) {
        searchRunnable?.let { handler.removeCallbacks(it) }
        searchRunnable = Runnable { performSearch(query) }
        // Запускаем новый запрос с задержкой в 2 секунды
        searchRunnable?.let { runnable ->
            handler.postDelayed(runnable, 2000)
        }
    }
    // Выполнение поискового запроса
    private fun performSearch(query: String) {
        lastQuery = query
        searchHistoryTracks.hideHistory()
        hideErrorPlaceholder()
        hideUpdateButton()
        recyclerView.visibility = View.GONE
        progressBarScreen.visibility = View.VISIBLE
        viewModel.searchTracks(query)
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
        searchHistoryTracks.hideHistory()
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
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("track", track)
        startActivity(intent)
    }
}