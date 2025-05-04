package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistViewBottomSheetBinding
import com.example.playlistmaker.createandeditplaylist.domain.models.Playlist

class PlayerAdapter(private val clickListener: (Playlist) -> Unit) : RecyclerView.Adapter<PlayerViewHolder>() {

    var playlists: MutableList<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlayerViewHolder(PlaylistViewBottomSheetBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val currentPlaylist = playlists[position]
        holder.bind(currentPlaylist)
        holder.itemView.setOnClickListener { clickListener(currentPlaylist) }
    }
}