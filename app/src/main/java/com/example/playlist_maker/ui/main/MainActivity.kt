package com.example.playlist_maker.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        setupNavigationListener(navController)
    }

    private fun setupNavigationListener(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.player_coordinator_layout,
                R.id.newPlaylistFragment,
                R.id.playerFragment,
                R.id.playlistMenuFragment -> {

                    binding.navSeparator.visibility = View.GONE
                    binding.navView.visibility = View.GONE
                }

                else -> {
                    binding.navSeparator.visibility = View.VISIBLE
                    binding.navView.visibility = View.VISIBLE
                }
            }
        }
    }
}
