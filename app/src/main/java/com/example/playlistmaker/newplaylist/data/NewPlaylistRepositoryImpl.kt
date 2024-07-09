package com.example.playlistmaker.newplaylist.data

import com.example.playlistmaker.database.convertors.EmptyPlaylistDbConvertor
import com.example.playlistmaker.database.dao.PlaylistDao
import com.example.playlistmaker.newplaylist.domain.api.NewPlaylistRepository
import com.example.playlistmaker.newplaylist.domain.models.Playlist

class NewPlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val emptyPlaylistDbConvertor: EmptyPlaylistDbConvertor
) : NewPlaylistRepository {
    override suspend fun insertPlaylist(playlist: Playlist): Long {
        return playlistDao.insert(emptyPlaylistDbConvertor.map(playlist))
    }
}