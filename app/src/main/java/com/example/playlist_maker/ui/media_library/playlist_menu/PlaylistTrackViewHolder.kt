package com.example.playlist_maker.ui.media_library.playlist_menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlist_maker.R
import com.example.playlist_maker.domein.player.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistTrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val trackImage: ImageView = itemView.findViewById(R.id.trackImage)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)

        val placeholder = R.drawable.placeholder_player
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(placeholder)
            .error(placeholder)
            .into(trackImage)
    }

    companion object {
        fun create(parent: ViewGroup): PlaylistTrackViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_track, parent, false)
            return PlaylistTrackViewHolder(view)
        }
    }
}