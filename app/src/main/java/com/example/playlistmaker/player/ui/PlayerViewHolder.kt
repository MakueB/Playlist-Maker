package com.example.playlistmaker.player.ui

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistViewBottomSheetBinding
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.utils.CommonUtils

class PlayerViewHolder(private val binding: PlaylistViewBottomSheetBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(playlist: Playlist) {
        val cornersInPx = CommonUtils.dpToPx(8f, itemView.context)
        val tracksNumber = playlist.trackList.size

        binding.playlistName.text = playlist.name
        binding.numberOfTracks.text = "$tracksNumber ${CommonUtils.getTrackWordForm(tracksNumber)}"

        Log.d("PlayerViewHolder", "Image URL: ${playlist.imageUrl}")

        if (playlist.imageUrl.isNullOrEmpty())
            binding.image.setImageResource(R.drawable.placeholder)
        else
            Glide.with(itemView)
                .load(playlist.imageUrl)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(cornersInPx))
                .error(R.drawable.placeholder) // Плейсхолдер на случай ошибки
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("PlayerViewHolder", "Glide Load Failed", e)
                        return false // Вернуть false для передачи ошибки дальше
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("PlayerViewHolder", "Glide Load Successful")
                        return false
                    }
                })
                .into(binding.image)

    }
}