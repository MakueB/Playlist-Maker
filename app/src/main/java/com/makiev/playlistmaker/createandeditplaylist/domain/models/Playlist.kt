package com.makiev.playlistmaker.createandeditplaylist.domain.models

import android.os.Parcelable
import com.makiev.playlistmaker.search.domain.models.Track
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val trackList: List<Track>
) : Parcelable
