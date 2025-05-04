package com.example.playlistmaker.library.ui.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistViewBinding
import com.example.playlistmaker.createplaylist.domain.models.Playlist

class PlaylistsAdapter(
    private val onItemClick: (Playlist) -> Unit,
    private val onItemLongClick: (Playlist) -> Boolean
) : RecyclerView.Adapter<PlaylistsViewHolder>() {
    var playlists: MutableList<Playlist> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistsViewHolder(PlaylistViewBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        val currentPlaylist = playlists[position]
        holder.bind(currentPlaylist)

        holder.itemView.setOnClickListener {
            onItemClick(currentPlaylist)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClick(currentPlaylist)
        }
    }
}