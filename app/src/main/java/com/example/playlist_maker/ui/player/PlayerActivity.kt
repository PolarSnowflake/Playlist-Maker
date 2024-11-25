package com.example.playlist_maker.ui.player

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.ActivityPlayerBinding
import com.example.playlist_maker.domein.player.Track
import com.example.playlist_maker.domein.playlist.Playlist
import com.example.playlist_maker.ui.media_library.playlists.NewPlaylistFragment
import com.example.playlist_maker.ui.media_library.playlists.PlaylistAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(intent.getSerializableExtra("track") as Track)
    }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var playlistAdapter: PlaylistAdapter

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                try {
                    contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    val currentPlaylistId = viewModel.currentPlaylistId
                    if (currentPlaylistId != null) {
                        viewModel.onImageSelected(currentPlaylistId, it.toString())
                    }
                } catch (e: SecurityException) {
                    Toast.makeText(
                        this,
                        "Не удалось получить доступ к изображению. Попробуйте снова.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupTrackObserver()
        setupFavoriteObserver()
        setupBottomSheet()
    }

    private fun setupUI() {
        binding.buttonPlay.setOnClickListener {
            viewModel.onPlayPauseClicked()
        }

        binding.likeButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }

        binding.addToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun setupTrackObserver() {
        viewModel.playerState.observe(this) { playerState ->
            binding.buttonPlay.setImageResource(
                if (playerState.isPlaying) R.drawable.pause else R.drawable.play
            )
            binding.currentTime.text = playerState.currentPlayTime
            updateTrackUI(playerState.track)
        }
    }

    // Логика кнопки "Нравится"
    private fun setupFavoriteObserver() {
        viewModel.isFavorite.observe(this) { isFavorite ->
            binding.likeButton.setImageResource(
                if (isFavorite) R.drawable.like_full else R.drawable.like
            )
        }
    }

    fun onPlaylistCreated() {
        viewModel.refreshPlaylists()
        bottomSheetBehavior.state =
            BottomSheetBehavior.STATE_EXPANDED // Оставляем BottomSheet открытым
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetContainer.root)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.peekHeight = 0

        val overlay = binding.overlay
        overlay.visibility = View.GONE

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.overlay.visibility = View.VISIBLE
                        binding.overlay.animate().alpha(1f).setDuration(200).start()
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.animate().alpha(0f).setDuration(200).withEndAction {
                            binding.overlay.visibility = View.GONE
                        }.start()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Прозрачность оверлея
                binding.overlay.alpha = slideOffset.coerceIn(0f, 1f)
            }
        })

        playlistAdapter = PlaylistAdapter(emptyList(), isCardMode = false) { playlist ->
            addTrackToPlaylist(playlist)
        }

        binding.bottomSheetContainer.rvPlaylists.apply {
            layoutManager = LinearLayoutManager(this@PlayerActivity)
            adapter = playlistAdapter
        }

        binding.bottomSheetContainer.btAddNewPlaylist.setOnClickListener {
            openNewPlaylistScreen()
        }

        viewModel.playlists.observe(this) { playlists ->
            playlistAdapter.updatePlaylists(playlists)
        }
    }

    private fun addTrackToPlaylist(playlist: Playlist) {
        val trackId = viewModel.playerState.value?.track?.trackId
        if (trackId != null) {
            viewModel.addTrackToPlaylist(trackId, playlist.id)
            Toast.makeText(this, "Трек добавлен в плейлист: ${playlist.name}", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this, "Ошибка добавления трека", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openNewPlaylistScreen() {
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, NewPlaylistFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun updateTrackUI(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.albumName.text = track.collectionName ?: "Unknown Album"
        binding.year.text = getYearFromDate(track.releaseDate)
        binding.genre.text = track.primaryGenreName
        binding.country.text = track.country
        binding.time.text = formatTrackTime(track.trackTime)

        // Картинка обложки
        val radius = resources.getDimensionPixelSize(R.dimen.player_image_corner_radius)
        Glide.with(this)
            .load(
                track.artworkUrl100.replace(
                    "100x100bb",
                    "512x512bb"
                )
            ) // Загружаем обложку в большем разрешении
            .apply(RequestOptions().transform(RoundedCorners(radius)))
            .placeholder(R.drawable.placeholder_playlists)
            .error(R.drawable.placeholder_playlists)
            .into(binding.playerImage)
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