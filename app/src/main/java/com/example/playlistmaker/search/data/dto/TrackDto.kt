package com.example.playlistmaker.search.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

@Parcelize
data class TrackDto(    val trackId: Int,
                        val trackName: String, // Название композиции
                        val artistName: String, // Имя исполнителя
                        val trackTimeMillis: Long, // Продолжительность трека
                        val artworkUrl100: String, // Ссылка на изображение обложки
                        val collectionName: String?,
                        val releaseDate: String?,
                        val primaryGenreName: String?,
                        val country: String?,
                        val previewUrl: String?
) : Parcelable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    fun getTimeInMmSs() =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
}
