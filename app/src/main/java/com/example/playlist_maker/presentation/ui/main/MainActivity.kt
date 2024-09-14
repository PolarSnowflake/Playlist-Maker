package com.example.playlist_maker.presentation.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlist_maker.R
import com.example.playlist_maker.presentation.ui.player.MediatekaActivity
import com.example.playlist_maker.presentation.ui.search.SearchActivity
import com.example.playlist_maker.presentation.ui.settings.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)

        if (sharedPreferences.getBoolean("NIGHT_MODE", false)) {
            setTheme(R.style.Theme_PlaylistMaker_Dark)
        } else {
            setTheme(R.style.Theme_PlaylistMaker_Light)
        }

        setContentView(R.layout.activity_main)

        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)

        button1.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener {
            val intent = Intent(this@MainActivity, MediatekaActivity::class.java)
            startActivity(intent)
        }

        button3.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}