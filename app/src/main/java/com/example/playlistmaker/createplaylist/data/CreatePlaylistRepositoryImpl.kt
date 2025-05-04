package com.example.playlistmaker.createplaylist.data

import com.example.playlistmaker.database.convertors.EmptyPlaylistDbConvertor
import com.example.playlistmaker.database.dao.PlaylistDao
import com.example.playlistmaker.createplaylist.domain.api.CreatePlaylistRepository
import com.example.playlistmaker.createplaylist.domain.models.Playlist

class CreatePlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val emptyPlaylistDbConvertor: EmptyPlaylistDbConvertor
) : CreatePlaylistRepository {
    override suspend fun savePlaylist(playlist: Playlist): Long {
        return playlistDao.insert(emptyPlaylistDbConvertor.map(playlist))
    }
}