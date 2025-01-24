package com.example.playlist_maker.ui.media_library.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentPlaylistsBinding
import com.example.playlist_maker.domein.playlist.Playlist
import com.example.playlist_maker.ui.media_library.MediaLibraryFragmentDirections
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var playlistAdapter: PlaylistAdapter

    companion object {
        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupCreatePlaylistButton()
        observeViewModel()
    }

    private fun openNewPlaylistScreen() {
        val bundle = Bundle().apply {
            putLong("playlistId", 0L) // Новый плейлист
            putString("name", null)
            putString("description", null)
            putString("coverPath", null)
        }
        findNavController().navigate(R.id.newPlaylistFragment, bundle)
    }

    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapter(emptyList(), isCardMode = true) { playlist ->
            onPlaylistClicked(playlist)
        }

        binding.rView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rView.adapter = playlistAdapter
    }

    private fun observeViewModel() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.updatePlaylists(playlists)
            binding.playlistsPlaceholder.root.visibility =
                if (playlists.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupCreatePlaylistButton() {
        binding.createPlaylistButton.setOnClickListener {
            openNewPlaylistScreen()
        }
    }

    private fun onPlaylistClicked(playlist: Playlist) {
        val parentNavController = requireParentFragment().findNavController()
        val action =
            MediaLibraryFragmentDirections.actionFragmentMediaLibraryToPlaylistMenuFragment(playlist.id)
        parentNavController.navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}