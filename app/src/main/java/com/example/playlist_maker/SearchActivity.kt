package com.example.playlist_maker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var searchBar: EditText
    private lateinit var clearButton: ImageButton
    private var searchText: String? = null
    private var cursorPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchBar = findViewById(R.id.searchBar)
        clearButton = findViewById(R.id.clear_button)

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                searchText = s?.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        clearButton.setOnClickListener {
            searchBar.text.clear()
            hideKeyboard()
        }

        val backButton: Button = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            finish()
        }

        // Восстановление состояния при создании активити
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("SEARCH_TEXT")
            cursorPosition = savedInstanceState.getInt("CURSOR_POSITION", 0)
            searchBar.setText(searchText)
            searchBar.setSelection(cursorPosition)
        }
    }

    // Сохранение состояния
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_TEXT", searchText)
        outState.putInt("CURSOR_POSITION", searchBar.selectionStart)
    }

    // Восстановление состояния
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString("SEARCH_TEXT")
        cursorPosition = savedInstanceState.getInt("CURSOR_POSITION", 0)
        searchBar.setText(searchText)
        searchBar.setSelection(cursorPosition)
    }

    // Метод для скрытия клавиатуры
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchBar.windowToken, 0)
    }
}