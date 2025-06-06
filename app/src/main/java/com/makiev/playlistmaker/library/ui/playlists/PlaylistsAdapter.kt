package com.makiev.playlistmaker.library.ui.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makiev.playlistmaker.databinding.PlaylistViewBinding
import com.makiev.playlistmaker.createandeditplaylist.domain.models.Playlist

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