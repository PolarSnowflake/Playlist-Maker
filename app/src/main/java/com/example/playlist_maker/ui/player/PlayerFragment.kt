package com.example.playlist_maker.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentPlayerBinding
import com.example.playlist_maker.domein.player.Track
import com.example.playlist_maker.domein.playlist.Playlist
import com.example.playlist_maker.ui.media_library.playlists.PlaylistAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(requireArguments().getSerializable("track") as Track)
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var playlistAdapter: PlaylistAdapter
    private var expandAfterPlaylistCreation = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupTrackObserver()
        setupFavoriteObserver()
        setupBottomSheet()

        parentFragmentManager.setFragmentResultListener(
            "playlist_result",
            viewLifecycleOwner
        ) { _, bundle ->
            val isPlaylistCreated = bundle.getBoolean("isPlaylistCreated", false)
            if (isPlaylistCreated) {
                expandAfterPlaylistCreation = true
                viewModel.refreshPlaylists()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideBottomSheet()
    }

    private fun setupUI() {
        binding.buttonPlay.setOnClickListener {
            viewModel.onPlayPauseClicked()
        }

        binding.likeButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.buttonBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.addToPlaylist.setOnClickListener {
            viewModel.refreshPlaylists()
            showBottomSheet(expanded = true)
        }
    }

    private fun setupTrackObserver() {
        viewModel.playerState.observe(viewLifecycleOwner) { playerState ->
            binding.buttonPlay.setImageResource(
                if (playerState.isPlaying) R.drawable.pause else R.drawable.play
            )
            binding.currentTime.text = playerState.currentPlayTime
            updateTrackUI(playerState.track)
        }
    }

    private fun setupFavoriteObserver() {
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            binding.likeButton.setImageResource(
                if (isFavorite) R.drawable.like_full else R.drawable.like
            )
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetContainer.root)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.overlay.visibility = View.GONE

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.overlay.visibility = View.VISIBLE
                        binding.overlay.alpha = 1f
                    }

                    BottomSheetBehavior.STATE_DRAGGING,
                    BottomSheetBehavior.STATE_SETTLING -> {
                        if (expandAfterPlaylistCreation) {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (!isAdded || _binding == null) return

                if (slideOffset >= 0f) {
                    val alpha = (slideOffset + 1) / 2
                    binding.overlay.alpha = alpha
                }
            }
        })

        playlistAdapter = PlaylistAdapter(emptyList(), isCardMode = false) { playlist ->
            addTrackToPlaylist(playlist)
        }

        binding.bottomSheetContainer.rvPlaylists.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playlistAdapter
        }

        binding.bottomSheetContainer.btAddNewPlaylist.setOnClickListener {
            hideBottomSheet()
            openNewPlaylistScreen()
        }

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.updatePlaylists(playlists)
            if (expandAfterPlaylistCreation) {
                expandAfterPlaylistCreation = false
                showBottomSheet(expanded = true)
            }
        }
    }

    private fun showBottomSheet(expanded: Boolean = false) {
        bottomSheetBehavior.state = if (expanded) {
            BottomSheetBehavior.STATE_EXPANDED
        } else {
            BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        binding.overlay.visibility = View.VISIBLE
        binding.overlay.alpha = 1f
    }

    private fun hideBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun addTrackToPlaylist(playlist: Playlist) {
        val trackId = viewModel.playerState.value?.track?.trackId
        if (trackId != null) {
            if (playlist.trackIds.contains(trackId)) {
                Toast.makeText(
                    requireContext(),
                    "Трек уже добавлен в плейлист: ${playlist.name}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.addTrackToPlaylist(trackId, playlist.id)
                Toast.makeText(
                    requireContext(),
                    "Трек добавлен в плейлист: ${playlist.name}",
                    Toast.LENGTH_SHORT
                ).show()
                hideBottomSheet()
            }
        } else {
            Toast.makeText(requireContext(), "Ошибка добавления трека", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openNewPlaylistScreen() {
        val action = PlayerFragmentDirections.actionPlayerFragmentToNewPlaylistFragment()
        findNavController().navigate(action)
    }

    private fun updateTrackUI(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.albumName.text = track.collectionName ?: "Unknown Album"
        binding.year.text = getYearFromDate(track.releaseDate)
        binding.genre.text = track.primaryGenreName
        binding.country.text = track.country
        binding.time.text = formatTrackTime(track.trackTime)

        val radius = resources.getDimensionPixelSize(R.dimen.player_image_corner_radius)
        Glide.with(this)
            .load(
                track.artworkUrl100.replace("100x100bb", "512x512bb")
            )
            .apply(RequestOptions().transform(RoundedCorners(radius)))
            .placeholder(R.drawable.placeholder_playlists)
            .error(R.drawable.placeholder_playlists)
            .into(binding.playerImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun formatTrackTime(trackTime: Long): String {
        val minutes = (trackTime / 1000) / 60
        val seconds = (trackTime / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun getYearFromDate(dateString: String): String {
        return dateString.take(4)
    }
}