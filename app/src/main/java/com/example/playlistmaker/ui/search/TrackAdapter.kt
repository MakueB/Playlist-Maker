package com.example.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.OnTrackClickListener
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackAdapter(private val clickListener: OnTrackClickListener) : RecyclerView.Adapter<TracksViewHolder>() {
    var tracks = mutableListOf<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TracksViewHolder(view)
    }

    override fun getItemCount() = tracks.size


    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        val currentTrack = tracks[position]
        holder.bind(currentTrack)
        holder.itemView.setOnClickListener { clickListener.onTrackClick(currentTrack) }
    }
}