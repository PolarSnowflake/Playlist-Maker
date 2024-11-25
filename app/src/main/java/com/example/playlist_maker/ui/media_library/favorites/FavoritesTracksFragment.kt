package com.example.playlist_maker.ui.media_library.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlist_maker.ui.search.TrackAdapter
import com.example.playlist_maker.domein.player.Track
import com.example.playlist_maker.databinding.FragmentFavoritesTracksBinding
import com.example.playlist_maker.ui.player.PlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesTracksFragment : Fragment() {

    private val viewModel: FavoritesTracksViewModel by viewModel()
    private var _binding: FragmentFavoritesTracksBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TrackAdapter

    companion object {
        fun newInstance() = FavoritesTracksFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter(emptyList()) { track ->
            navigateToPlayer(track)
        }
        binding.favoritesTracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesTracksRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoritesState.Empty -> showPlaceholder()
                is FavoritesState.Content -> showTracks(state.tracks)
            }
        }
    }

    private fun showPlaceholder() {
        binding.placeholder.visibility = View.VISIBLE
        binding.favoritesTracksRecyclerView.visibility = View.GONE
    }

    private fun showTracks(tracks: List<Track>) {
        binding.placeholder.visibility = View.GONE
        binding.favoritesTracksRecyclerView.visibility = View.VISIBLE
        adapter.updateTracks(tracks)
    }

    private fun navigateToPlayer(track: Track) {
        val intent = Intent(requireContext(), PlayerActivity::class.java)
        intent.putExtra("track", track)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}