package com.example.playlist_maker.ui.media_library.playlist_menu

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentPlaylistMenuBinding
import com.example.playlist_maker.domein.player.Track
import com.example.playlist_maker.domein.playlist.Playlist
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistMenuFragment : Fragment() {

    private var _binding: FragmentPlaylistMenuBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistMenuViewModel by viewModel { parametersOf(requireActivity().application) }
    private val args: PlaylistMenuFragmentArgs by navArgs()

    companion object {
        private const val SHARE_REQUEST_CODE = 1001
    }

    private var isMenuVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playlistId = args.playlistId
        viewModel.loadPlaylist(playlistId)

        setupUI()
        observeViewModel()
    }

    private var initialY = 0f
    private var isDragging = false

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.share.setOnClickListener {
            showOverlay()
            viewModel.sharePlaylist()
        }

        binding.menu.setOnClickListener {
            toggleMenu()
        }

        binding.overlay.setOnClickListener {
            closeAllOverlays()
        }

        binding.editingBottomSheet.setOnTouchListener { _, event ->
            binding.editingBottomSheet.parent.requestDisallowInterceptTouchEvent(true)
            return@setOnTouchListener handleTouchOnBottomSheet(event)
        }

        binding.deletePl.setOnClickListener {
            closeMenu()
            confirmDeletePlaylist()
        }

        binding.sharePl.setOnClickListener {
            closeMenu()
            viewModel.sharePlaylist()
        }

        binding.editPlaylist.setOnClickListener {
            closeMenu()
            navigateToEditPlaylist()
        }
    }

    private fun observeViewModel() {
        viewModel.playlistLiveData.observe(viewLifecycleOwner) { playlist ->
            playlist?.let { updatePlaylistUI(it) }
        }

        viewModel.tracksLiveData.observe(viewLifecycleOwner) { tracks ->
            val sortedTracks = tracks.reversed()

            if (sortedTracks.isNotEmpty()) {
                binding.rvTrackList.visibility = View.VISIBLE
                binding.noTracksMassage.visibility = View.GONE
                setupTrackList(sortedTracks)
            } else {
                binding.rvTrackList.visibility = View.GONE
                binding.noTracksMassage.visibility = View.VISIBLE
            }
            updatePlaylistUI(viewModel.playlistLiveData.value!!)
        }

        viewModel.shareText.observe(viewLifecycleOwner) { text ->
            text?.let { shareText(it) }
        }

        viewModel.messageLiveData.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        }
    }

    private fun updatePlaylistUI(playlist: Playlist) {
        binding.playlistName.text = playlist.name
        binding.plName.text = playlist.name

        binding.playlistDescription.text = playlist.description.ifEmpty { "" }

        val tracks = viewModel.tracksLiveData.value.orEmpty()
        val totalDurationMinutes = tracks.sumOf { (it.trackTime / 1000 / 60).toInt() }

        binding.durationAllTracks.text = minutesEndings(totalDurationMinutes)
        binding.numberOfTracks.text = tracksEndings(tracks.size)
        binding.quantityTracks.text = tracksEndings(tracks.size)

        if (playlist.coverPath.isNullOrEmpty()) {
            binding.playerCover.setImageResource(R.drawable.placeholder_player)
            binding.album.setImageResource(R.drawable.placeholder_player)
        } else {
            Glide.with(this)
                .load(playlist.coverPath)
                .placeholder(R.drawable.placeholder_player)
                .error(R.drawable.placeholder_player)
                .into(binding.playerCover)

            Glide.with(this)
                .load(playlist.coverPath)
                .placeholder(R.drawable.placeholder_player)
                .error(R.drawable.placeholder_player)
                .into(binding.album)
        }
    }

    private fun tracksEndings(quantityTracks: Int): String {
        val ending = when {
            quantityTracks == 0 -> "треков"
            quantityTracks % 100 in 11..14 -> "треков"
            quantityTracks % 10 == 1 -> "трек"
            quantityTracks % 10 in 2..4 -> "трека"
            else -> "треков"
        }
        return "$quantityTracks $ending"
    }

    private fun minutesEndings(count: Int): String {
        val ending = when {
            count == 0 -> "минут"
            count % 100 in 11..14 -> "минут"
            count % 10 == 1 -> "минута"
            count % 10 in 2..4 -> "минуты"
            else -> "минут"
        }
        return "$count $ending"
    }

    private fun setupTrackList(tracks: List<Track>) {
        val adapter = PlaylistTrackAdapter(
            tracks,
            onItemClick = { track -> navigateToPlayer(track) },
            onItemLongClick = { track -> showDeleteTrackDialog(track) }
        )
        binding.rvTrackList.adapter = adapter
    }

    private fun navigateToPlayer(track: Track) {
        val action =
            PlaylistMenuFragmentDirections.actionPlaylistMenuFragmentToPlayerFragment(track)
        findNavController().navigate(action)
    }

    private fun navigateToEditPlaylist() {
        Log.d("PlaylistMenuFragment", "Navigating to edit playlist")
        val playlist = viewModel.playlistLiveData.value
        if (playlist != null) {
            val bundle = Bundle().apply {
                putLong("playlistId", playlist.id)
                putString("name", playlist.name)
                putString("description", playlist.description)
                putString("coverPath", playlist.coverPath)
            }
            findNavController().navigate(R.id.newPlaylistFragment, bundle)
        } else {
            Log.e("PlaylistMenuFragment", "Error loading playlist for editing")
            Toast.makeText(
                requireContext(),
                getString(R.string.error_loading_playlist),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.confirmDeleteTrack))
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                viewModel.deleteTrackFromPlaylist(track)
                dialog.dismiss()
            }
            .show()
    }

    private fun confirmDeletePlaylist() {
        val playlistName = viewModel.playlistLiveData.value?.name.orEmpty()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.confirmDeletePlaylistWithName, playlistName))
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deletePlaylist()
                findNavController().popBackStack()
            }
            .show()
    }

    private fun toggleMenu() {
        if (isMenuVisible) {
            closeMenu()
        } else {
            showMenu()
        }
    }

    private fun showOverlay() {
        binding.overlay.visibility = View.VISIBLE
    }

    private fun closeAllOverlays() {
        if (isMenuVisible) {
            closeMenu()
        }
        binding.overlay.visibility = View.GONE
    }

    private fun shareText(text: String) {
        val playlist = viewModel.playlistLiveData.value
        val tracks = viewModel.tracksLiveData.value.orEmpty()
        val sortedTracks = tracks.reversed()
        val trackList = sortedTracks.mapIndexed { index, track ->
            "${index + 1}. ${track.artistName} - ${track.trackName} (${viewModel.formatTrackTime(track.trackTime)})"
        }.joinToString("\n")

        val shareText = """
        Название плейлиста: ${playlist?.name}
        Описание: ${playlist?.description ?: ""}
        Количество треков: ${tracks.size}
        $trackList
    """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.share)),
            SHARE_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SHARE_REQUEST_CODE) {
            closeAllOverlays()
        }
    }

    private fun closeMenu() {
        if (_binding == null) return
        _binding?.overlay?.visibility = View.GONE

        val layoutParams = _binding?.editingBottomSheet?.layoutParams
        val initialHeight = _binding?.editingBottomSheet?.height ?: 0
        val targetHeight = 0

        ValueAnimator.ofInt(initialHeight, targetHeight).apply {
            duration = 300
            addUpdateListener { animator ->
                layoutParams?.height = animator.animatedValue as Int
                _binding?.editingBottomSheet?.layoutParams = layoutParams
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    _binding?.editingBottomSheet?.visibility = View.GONE
                }
            })
            start()
        }
        isMenuVisible = false
    }

    private fun showMenu() {
        if (_binding == null) return
        _binding?.overlay?.visibility = View.VISIBLE
        _binding?.editingBottomSheet?.visibility = View.VISIBLE

        val targetHeight = (383 * resources.displayMetrics.density).toInt()
        val layoutParams = _binding?.editingBottomSheet?.layoutParams
        val initialHeight = _binding?.editingBottomSheet?.height ?: 0

        ValueAnimator.ofInt(initialHeight, targetHeight).apply {
            duration = 300
            addUpdateListener { animator ->
                layoutParams?.height = animator.animatedValue as Int
                _binding?.editingBottomSheet?.layoutParams = layoutParams
            }
            start()
        }
        isMenuVisible = true
    }

    // Обработчик свайпов
    private fun handleTouchOnBottomSheet(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialY = event.y
                isDragging = false
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isDragging && event.y - initialY > 10) {
                    isDragging = true
                }
                return false
            }

            MotionEvent.ACTION_UP -> {
                if (isDragging && event.y - initialY > binding.editingBottomSheet.height / 3) {
                    closeMenu()
                }
                return true
            }

            else -> return false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}