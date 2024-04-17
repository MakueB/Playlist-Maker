package com.example.playlistmaker.ui.activities.search

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.utils.CommonUtils
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.domain.models.Track

class TracksViewHolder (private val binding: TrackViewBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind (track: Track){
        val cornersInPx = CommonUtils.dpToPx(2f, itemView.context)

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