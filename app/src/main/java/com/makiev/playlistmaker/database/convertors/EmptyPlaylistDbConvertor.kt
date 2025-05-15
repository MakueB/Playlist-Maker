package com.makiev.playlistmaker.database.convertors

import com.makiev.playlistmaker.database.PlaylistEntity
import com.makiev.playlistmaker.createandeditplaylist.domain.models.Playlist

class EmptyPlaylistDbConvertor {
    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            id = playlistEntity.id,
            name = playlistEntity.name,
            description = playlistEntity.description,
            imageUrl = playlistEntity.imageUri,
            trackList = emptyList()
        )
    }

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            imageUri = playlist.imageUrl
        )
    }
}