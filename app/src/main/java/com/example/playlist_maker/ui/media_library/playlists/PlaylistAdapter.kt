package com.example.playlist_maker.ui.media_library.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker.R
import com.example.playlist_maker.domein.playlist.Playlist

class PlaylistAdapter(
    private var playlists: List<Playlist>,
    private val isCardMode: Boolean,
    private val onPlaylistClicked: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutId = if (isCardMode) R.layout.item_playlist_card else R.layout.item_playlist
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return PlaylistViewHolder(view, isCardMode)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            onPlaylistClicked(playlist)
        }
    }

    override fun getItemCount() = playlists.size

    fun updatePlaylistCover(playlistId: Long, newCoverPath: String) {
        playlists = playlists.map { playlist ->
            if (playlist.id == playlistId) {
                playlist.copy(coverPath = newCoverPath)
            } else {
                playlist
            }
        }
        notifyDataSetChanged()
    }
}