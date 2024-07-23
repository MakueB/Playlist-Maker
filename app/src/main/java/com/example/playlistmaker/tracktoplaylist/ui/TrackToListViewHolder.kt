package com.example.playlistmaker.tracktoplaylist.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistViewBottomSheetBinding
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.utils.CommonUtils

class TrackToListViewHolder(private val binding: PlaylistViewBottomSheetBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(playlist: Playlist) {
        val cornersInPx = CommonUtils.dpToPx(8f, itemView.context)
        val tracksNumber = playlist.trackList.size

        binding.playlistName.text = playlist.name
        binding.numberOfTracks.text = "$tracksNumber ${CommonUtils.getTrackWordForm(tracksNumber)}"

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