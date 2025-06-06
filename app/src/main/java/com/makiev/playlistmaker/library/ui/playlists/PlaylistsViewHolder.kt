package com.makiev.playlistmaker.library.ui.playlists

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.makiev.playlistmaker.R
import com.makiev.playlistmaker.databinding.PlaylistViewBinding
import com.makiev.playlistmaker.createandeditplaylist.domain.models.Playlist
import com.makiev.playlistmaker.utils.Utils

class PlaylistsViewHolder(private val binding: PlaylistViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(playlist: Playlist) {
        val cornersInPx = Utils.dpToPx(8f, itemView.context)
        val tracksNumber = playlist.trackList.size

        binding.playlistName.text = playlist.name
        binding.tracksNumber.text = "$tracksNumber ${Utils.getTrackWordForm(tracksNumber)}"


        if (playlist.imageUrl.isNullOrEmpty())
            binding.playlistCover.setImageResource(R.drawable.placeholder)
        else
            Glide.with(itemView)
                .load(playlist.imageUrl)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(cornersInPx))
                .into(binding.playlistCover)
    }
}


