package com.makiev.playlistmaker.player.ui

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.makiev.playlistmaker.R
import com.makiev.playlistmaker.databinding.PlaylistViewBottomSheetBinding
import com.makiev.playlistmaker.createandeditplaylist.domain.models.Playlist
import com.makiev.playlistmaker.utils.Utils

class PlayerViewHolder(private val binding: PlaylistViewBottomSheetBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(playlist: Playlist) {
        val cornersInPx = Utils.dpToPx(8f, itemView.context)
        val tracksNumber = playlist.trackList.size

        binding.playlistName.text = playlist.name
        binding.numberOfTracks.text = "$tracksNumber ${Utils.getTrackWordForm(tracksNumber)}"

        Log.d("PlayerViewHolder", "Image URL: ${playlist.imageUrl}")

        if (playlist.imageUrl.isNullOrEmpty())
            binding.image.setImageResource(R.drawable.placeholder)
        else
            Glide.with(itemView)
                .load(playlist.imageUrl)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(cornersInPx))
                .into(binding.image)
    }
}