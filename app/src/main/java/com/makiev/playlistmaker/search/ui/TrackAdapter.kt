package com.makiev.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makiev.playlistmaker.databinding.TrackViewBinding
import com.makiev.playlistmaker.search.domain.models.Track

class TrackAdapter(private val actionListener: TrackActionListener) : RecyclerView.Adapter<TracksViewHolder>() {
    var tracks = mutableListOf<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return TracksViewHolder(TrackViewBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount() = tracks.size


    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        val currentTrack = tracks[position]
        holder.bind(currentTrack)
        holder.itemView.setOnClickListener {  actionListener.onTrackClick(currentTrack) }
        holder.itemView.setOnLongClickListener { actionListener.onTrackLongClick(currentTrack) }
    }
}