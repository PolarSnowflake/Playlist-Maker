package com.example.playlist_maker.domein.playlist_menu

import android.util.Log
import com.example.playlist_maker.domein.player.Track
import com.example.playlist_maker.domein.playlist.Playlist
import com.example.playlist_maker.domein.playlist.PlaylistRepository
import com.example.playlist_maker.domein.search.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion

class PlaylistMenuInteractorImpl(
    private val playlistRepository: PlaylistRepository,
    private val trackRepository: TrackRepository
) : PlaylistMenuInteractor {

    override fun getPlaylistById(playlistId: Long): Flow<Playlist> {
        return playlistRepository.getPlaylistByIdFlow(playlistId)
    }

    override fun getTracksFromPlaylist(trackIds: List<Long>): Flow<List<Track>> {
        return flow {
            if (trackIds.isEmpty()) {
                emit(emptyList<Track>())
                return@flow
            }

            val tracks = mutableListOf<Track>()
            for (trackId in trackIds) {
                try {
                    val result =
                        trackRepository.searchTracks(trackId.toString()).firstOrNull()?.getOrNull()
                            ?: emptyList()
                    tracks.addAll(result)

                } catch (e: Exception) {
                    tracks.add(
                        Track(
                            trackId = trackId,
                            trackName = "Unknown",
                            artistName = "Unknown",
                            trackTime = 0L,
                            artworkUrl100 = "",
                            collectionName = "",
                            releaseDate = "",
                            primaryGenreName = "",
                            country = "",
                            previewUrl = ""
                        )
                    )
                }
            }
            emit(tracks)
        }
            .catch { exception ->
                emit(emptyList<Track>())
            }
            .onCompletion {
                Log.d("PlaylistMenuInteractor", "Completed fetching tracks")
            }
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long) {
        playlistRepository.removeTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun deletePlaylistById(playlistId: Long) {
        playlistRepository.deletePlaylistById(playlistId)
    }
}