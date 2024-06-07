package com.example.playlist_maker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        val isNightMode = sharedPreferences.getBoolean("NIGHT_MODE", false)
        if (isNightMode) {
            setTheme(R.style.Theme_PlaylistMaker_Dark)
        } else {
            setTheme(R.style.Theme_PlaylistMaker_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val switch: Switch = findViewById(R.id.switch1)
        switch.isChecked = isNightMode

        switch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("NIGHT_MODE", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                setTheme(R.style.Theme_PlaylistMaker_Dark)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                setTheme(R.style.Theme_PlaylistMaker_Light)
            }
            recreate()
        }

        val backButton: Button = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}