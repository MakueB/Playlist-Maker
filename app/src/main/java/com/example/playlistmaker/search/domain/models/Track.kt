package com.example.playlistmaker.search.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track (
    val trackId: Int,
    val playlistId: Long,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackDuration: String, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
    var isFavorite: Boolean = false
    ) : Parcelable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}