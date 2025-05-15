package com.makiev.playlistmaker.search.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.makiev.playlistmaker.utils.Utils
import com.makiev.playlistmaker.R
import com.makiev.playlistmaker.databinding.TrackViewBinding
import com.makiev.playlistmaker.search.domain.models.Track

class TracksViewHolder (private val binding: TrackViewBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind (track: Track){
        val cornersInPx = Utils.dpToPx(2f, itemView.context)

        binding.trackNameTextView.text = track.trackName
        binding.artistNameTextView.text = track.artistName
        binding.trackTimeTextView.text = track.trackDuration

        if (track.artworkUrl100.isEmpty())
            binding.artWorkImageView.setImageResource(R.drawable.placeholder)
        else
            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(cornersInPx))
                .into(binding.artWorkImageView)
    }
}