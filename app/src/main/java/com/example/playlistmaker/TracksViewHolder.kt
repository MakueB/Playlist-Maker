package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TracksViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackNameTextView: TextView = itemView.findViewById(R.id.trackNameTextView)
    private val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
    private val trackTimeTextView: TextView = itemView.findViewById(R.id.trackTimeTextView)
    private val artWorkImageView: ImageView = itemView.findViewById(R.id.artWorkImageView)

    private fun convert(timeInMillis: Long) = SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeInMillis)

    fun bind (track: Track){
        val cornersInPx = dpToPx(2f,itemView.context)

        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackTimeTextView.text = convert(track.trackTimeMillis)

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

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}