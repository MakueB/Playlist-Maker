package com.makiev.playlistmaker.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.makiev.playlistmaker.R

object ImageLoader {
    fun loadImage(
        context: Context,
        imageView: ImageView,
        imageUrl: String?,
        cornerRadiusDp: Float = 8f,
        placeholderResId: Int = R.drawable.placeholder
    ) {
        val cornerPx = Utils.dpToPx(cornerRadiusDp, context)

        Glide.with(context)
            .load(imageUrl)
            .placeholder(placeholderResId)
            .centerCrop()
            .transform(RoundedCorners(cornerPx))
            .into(imageView)
    }

    fun clear(context: Context, imageView: ImageView) {
        Glide.with(context).clear(imageView)
    }
}