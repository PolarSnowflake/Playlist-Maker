package com.example.playlist_maker.ui.main

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.R

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(application)
        ).get(MainViewModel::class.java)

        // Установка темы
        viewModel.themeLiveData.observe(this) { isNightMode ->
            if (isNightMode) {
                setTheme(R.style.Theme_PlaylistMaker_Dark)
            } else {
                setTheme(R.style.Theme_PlaylistMaker_Light)
            }
        }

        setContentView(R.layout.activity_main)

        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)

        // Слушатели
        button1.setOnClickListener {
            viewModel.navigateToSearch(this)
        }

        button2.setOnClickListener {
            viewModel.navigateToMediateka(this)
        }

        button3.setOnClickListener {
            viewModel.navigateToSettings(this)
        }
    }
}