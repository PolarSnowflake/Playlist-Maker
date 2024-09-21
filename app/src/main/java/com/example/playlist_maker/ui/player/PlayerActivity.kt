package com.example.playlist_maker.ui.player

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlist_maker.R
import com.example.playlist_maker.data.player.PlayerState
import com.example.playlist_maker.domein.player.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class PlayerActivity : AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var currentPlayTimeTextView: TextView

    // Извлечение трека из Intent и передача его в ViewModel через Koin
    private val track by lazy { intent.getSerializableExtra("track") as Track }
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        currentPlayTimeTextView = findViewById(R.id.current_time)
        playButton = findViewById(R.id.button_play)

        viewModel.playerState.observe(this) { playerState ->
            // Обновление UI в зависимости от состояния
            if (playerState.isPlaying) {
                playButton.setImageResource(R.drawable.pause)
            } else {
                playButton.setImageResource(R.drawable.play)
            }
            currentPlayTimeTextView.text = playerState.currentPlayTime
            updateTrackUI(playerState.track)
        }

        // Логика кнопки "Play/Pause"
        playButton.setOnClickListener {
            viewModel.onPlayPauseClicked()
        }

        // Кнопка "Назад"
        val backButton: Button = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun updateTrackUI(track: Track) {
        val coverImageView: ImageView = findViewById(R.id.player_image)
        val trackNameTextView: TextView = findViewById(R.id.track_name)
        val artistNameTextView: TextView = findViewById(R.id.artist_name)
        val collectionNameTextView: TextView = findViewById(R.id.album_name)
        val releaseDateTextView: TextView = findViewById(R.id.year)
        val genreTextView: TextView = findViewById(R.id.genre)
        val countryTextView: TextView = findViewById(R.id.country)
        val trackTimeTextView: TextView = findViewById(R.id.time)

        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        collectionNameTextView.text = track.collectionName ?: "Unknown Album"
        releaseDateTextView.text = getYearFromDate(track.releaseDate)
        genreTextView.text = track.primaryGenreName
        countryTextView.text = track.country
        trackTimeTextView.text = formatTrackTime(track.trackTime)

        // Картинка обложки
        val radius = resources.getDimensionPixelSize(R.dimen.player_image_corner_radius)
        Glide.with(this)
            .load(track.artworkUrl100.replace("100x100bb", "512x512bb"))
            .apply(RequestOptions().transform(RoundedCorners(radius)))
            .into(coverImageView)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    // Формат времени
    private fun formatTrackTime(trackTime: Long): String {
        val minutes = (trackTime / 1000) / 60
        val seconds = (trackTime / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    // Обрезаем дату
    private fun getYearFromDate(dateString: String): String {
        return dateString.take(4) //Первые 4 символа (год)
    }
}