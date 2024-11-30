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
        observeViewModel()
        setupCreatePlaylistButton()
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
            findNavController().navigate(R.id.action_fragment_media_library_to_newPlaylistFragment)
        }
    }

    private fun onPlaylistClicked(playlist: Playlist) {
        //
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}