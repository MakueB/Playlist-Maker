package com.example.playlistmaker.library.ui.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistViewBinding
import com.example.playlistmaker.newplaylist.domain.models.Playlist

class PlaylistsAdapter : RecyclerView.Adapter<PlaylistsViewHolder>() {
    var playlists: MutableList<Playlist> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistsViewHolder(PlaylistViewBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        val currentPlaylist = playlists[position]
        holder.bind(currentPlaylist)
        //holder.itemView.setOnClickListener {  } //возможно, нужно будет добавить переход к плейлисту
    }
}