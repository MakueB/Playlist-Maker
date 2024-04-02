package com.example.playlistmaker.ui.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.utils.CommonUtils
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TracksViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackNameTextView: TextView = itemView.findViewById(R.id.trackNameTextView)
    private val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
    private val trackTimeTextView: TextView = itemView.findViewById(R.id.trackTimeTextView)
    private val artWorkImageView: ImageView = itemView.findViewById(R.id.artWorkImageView)

    fun bind (track: Track){
        val cornersInPx = CommonUtils.dpToPx(2f, itemView.context)

        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackTimeTextView.text = CommonUtils.formatMillisToMmSs(track.trackTimeMillis)

        if (track.artworkUrl100.isEmpty())
            artWorkImageView.setImageResource(R.drawable.placeholder)
        else
            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(cornersInPx))
                .into(artWorkImageView)
    }
}