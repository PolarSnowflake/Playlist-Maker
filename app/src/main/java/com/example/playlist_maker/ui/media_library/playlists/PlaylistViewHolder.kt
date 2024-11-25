package com.example.playlist_maker.ui.media_library.playlists

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlist_maker.R
import com.example.playlist_maker.domein.playlist.Playlist

class PlaylistViewHolder(itemView: View, private val isCardMode: Boolean) :
    RecyclerView.ViewHolder(itemView) {
    private val playlistImage: ImageView = itemView.findViewById(
        if (isCardMode) R.id.iv_playlist_big_image else R.id.iv_playlist_image
    )
    private val playlistName: TextView = itemView.findViewById(
        if (isCardMode) R.id.name_playlist_card else R.id.tv_playlist_name
    )
    private val playlistTracksCount: TextView? = itemView.findViewById(
        if (isCardMode) R.id.tracks_playlist_card else R.id.tv_quantity_of_tracks
    )

    fun bind(playlist: Playlist) {
        playlistName.text = playlist.name
        playlistTracksCount?.text = "${playlist.trackCount} треков"

        if (playlist.coverPath.isNullOrEmpty()) {
            playlistImage.setImageResource(R.drawable.placeholder_playlists)
        } else {
            Glide.with(itemView.context)
                .load(playlist.coverPath)
                .placeholder(R.drawable.placeholder_playlists)
                .into(playlistImage)
        }

        if (!isCardMode) {
            playlistImage.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }
}