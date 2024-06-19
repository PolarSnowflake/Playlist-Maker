package com.example.playlist_maker

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

        recyclerView.visibility = View.GONE
        errorPlaceholderLayout.visibility = View.GONE

        trackAdapter = TrackAdapter(emptyList()) // Начинаем с пустого списка треков
        recyclerView.adapter = trackAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // настройка событий для поиска
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (s.isNullOrEmpty()) {
                    recyclerView.visibility = View.GONE
                    hideErrorPlaceholder()
                    hideUpdateButton()
                } else {
                    performSearch(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        //кнопка "Готово" на клавиатуре
        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(searchBar.text.toString())
                hideKeyboard()
                true
            } else {
                false
            }
        }

        //кнопка "Очистить поисковый запрос"
        clearButton.setOnClickListener {
            searchBar.text.clear()
            hideKeyboard()
            recyclerView.visibility = View.GONE
            hideErrorPlaceholder()
            hideUpdateButton()
        }

        // кнопка "Обновить"
        updateButton.setOnClickListener {
            retryLastSearch()
        }

        // кнопка "Назад"
        val backButton: Button = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            finish()
        }

        // Восстановление состояния при создании активити
        if (savedInstanceState != null) {
            val searchText = savedInstanceState.getString("SEARCH_TEXT")
            searchBar.setText(searchText)
            searchBar.setSelection(searchText?.length ?: 0)
        }
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
    }

    // выполнение поискового запроса
    private fun performSearch(query: String) {
        lastQuery = query
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

    // повтор последнего неудавшегося запроса
    private fun retryLastSearch() {
        performSearch(lastQuery)
    }

    //плейсхолдер "Ничего не найдено"
    private fun showNoResultsPlaceholder() {
        trackAdapter.updateTracks(emptyList())
        recyclerView.visibility = View.GONE
        errorPlaceholderLayout.visibility = View.VISIBLE
        placeholderError.visibility = View.VISIBLE
        placeholderMessage.visibility = View.VISIBLE
        updateButtonLayout.visibility = View.GONE
        placeholderMessage.text = getString(R.string.nothing_found)
        placeholderError.setImageResource(R.drawable.not_found_icon)
    }

    // плейсхолдер "Ошибка подключения"
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

    // скрытие плейсхолдера ошибки
    private fun hideErrorPlaceholder() {
        errorPlaceholderLayout.visibility = View.GONE
    }

    // скрытие кнопки "Обновить"
    private fun hideUpdateButton() {
        updateButtonLayout.visibility = View.GONE
    }

    // скрытие клавиатуры
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchBar.windowToken, 0)
    }
}