package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

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