package com.example.playlist_maker.ui.media_library.playlist_menu

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker.domein.player.Track

class PlaylistTrackAdapter(
    private var tracks: List<Track>,
    private val onItemClick: (Track) -> Unit,
    private val onItemLongClick: (Track) -> Unit
) : RecyclerView.Adapter<PlaylistTrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistTrackViewHolder {
        return PlaylistTrackViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: PlaylistTrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener { onItemClick(track) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(track)
            true
        }
    }

    override fun getItemCount(): Int = tracks.size
}