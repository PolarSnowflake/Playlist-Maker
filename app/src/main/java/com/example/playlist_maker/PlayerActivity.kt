package com.example.playlist_maker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class PlayerActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playButton: ImageButton
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var currentPlayTimeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // Данные трека из Intent
        val track = intent.getSerializableExtra("track") as Track

        val coverImageView: ImageView = findViewById(R.id.player_image)
        val trackNameTextView: TextView = findViewById(R.id.track_name)
        val artistNameTextView: TextView = findViewById(R.id.artist_name)
        val collectionNameTextView: TextView = findViewById(R.id.album_name)
        val releaseDateTextView: TextView = findViewById(R.id.year)
        val genreTextView: TextView = findViewById(R.id.genre)
        val countryTextView: TextView = findViewById(R.id.country)
        val trackTimeTextView: TextView = findViewById(R.id.time)
        val backButton: Button = findViewById(R.id.button_back)
        val currentPlayTimeTextView: TextView = findViewById(R.id.current_time)
        playButton = findViewById(R.id.button_play)

        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        collectionNameTextView.text = track.collectionName ?: "Unknown Album"
        releaseDateTextView.text = getYearFromDate(track.releaseDate)
        genreTextView.text = track.primaryGenreName
        countryTextView.text = track.country
        trackTimeTextView.text = formatTrackTime(track.trackTime)
        currentPlayTimeTextView.text = "0:00" // Начальное значение времени воспр.

        // Картинка обложки
        val radius = resources.getDimensionPixelSize(R.dimen.player_image_corner_radius)
        Glide.with(this)
            .load(track.artworkUrl100.replace("100x100bb", "512x512bb"))
            .apply(RequestOptions().transform(RoundedCorners(radius)))
            .into(coverImageView)

        // MediaPlayer
        mediaPlayer = MediaPlayer().apply {
            setDataSource(track.previewUrl)
            prepare()
        }

        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val currentPosition = mediaPlayer.currentPosition / 1000
                    currentPlayTimeTextView.text = String.format(
                        "%02d:%02d",
                        currentPosition / 60,
                        currentPosition % 60
                    )
                    handler.postDelayed(this, 1000)
                }
            }
        }

        // Логика кнопки "Play/Pause"
        playButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                playButton.setImageResource(R.drawable.play)
                handler.removeCallbacks(runnable) // Останавливаем обновление текущего времени
            } else {
                if (mediaPlayer.currentPosition == mediaPlayer.duration) {
                    mediaPlayer.seekTo(0) // Начинаем с начала, если трек был проигран до конца
                }
                mediaPlayer.start()
                playButton.setImageResource(R.drawable.pause)
                handler.post(runnable) // Запускаем обновление текущего времени
            }
        }

        // Cлушатель окончания трека
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.play)
            currentPlayTimeTextView.text = "00:00" // Сбрасываем время воспроизведения
            handler.removeCallbacks(runnable) // Останавливаем обновление текущего времени
        }

        // Кнопка "Назад"
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            playButton.setImageResource(R.drawable.play) // Меняем иконку на "Play"
            handler.removeCallbacks(runnable) // Останавливаем обновление текущего времени
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release() // Освобождаем ресурсы MediaPlayer
        handler.removeCallbacks(runnable) // Останавливаем обновление времени
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