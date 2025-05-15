package com.makiev.playlistmaker.createandeditplaylist.data

import com.makiev.playlistmaker.database.convertors.EmptyPlaylistDbConvertor
import com.makiev.playlistmaker.database.dao.PlaylistDao
import com.makiev.playlistmaker.createandeditplaylist.domain.api.CreatePlaylistRepository
import com.makiev.playlistmaker.createandeditplaylist.domain.models.Playlist

class CreatePlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val emptyPlaylistDbConvertor: EmptyPlaylistDbConvertor
) : CreatePlaylistRepository {
    override suspend fun savePlaylist(playlist: Playlist): Long {
        return playlistDao.insert(emptyPlaylistDbConvertor.map(playlist))
    }
}