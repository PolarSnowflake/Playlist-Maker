package com.example.playlist_maker.ui.media_library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.ActivityMediaLibraryBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MediaLibraryActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewPager
        tabLayout = binding.tabMediaLibrary

        val adapter = MediaLibraryPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        tabMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favorites_tracks)
                1 -> tab.text = getString(R.string.playlists)
                else -> null
            }
        }.apply {
            attach()
        }

        // Кнопка "Назад"
        binding.buttonBack.setOnClickListener {
            finish()
        }
    }
}