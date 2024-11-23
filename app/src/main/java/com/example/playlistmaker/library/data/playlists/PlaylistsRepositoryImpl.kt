package com.example.playlistmaker.library.data.playlists

import com.example.playlistmaker.database.PlaylistTrackEntity
import com.example.playlistmaker.database.convertors.EmptyPlaylistDbConvertor
import com.example.playlistmaker.database.convertors.TrackDbConvertor
import com.example.playlistmaker.database.dao.PlaylistDao
import com.example.playlistmaker.database.dao.PlaylistTrackDao
import com.example.playlistmaker.library.domain.playlists.api.PlaylistsRepository
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val playlistDbConvertor: EmptyPlaylistDbConvertor,
    private val trackDbConvertor: TrackDbConvertor
) : PlaylistsRepository {
    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlistDbConvertor.map(playlist))
    }

    override suspend fun getPlaylistsAll(): Flow<List<Playlist>> = flow {
        val playlistsDb = playlistDao.getAllPlaylists() // Получение всех плейлистов из базы
        val playlistsWithTracks = playlistsDb.map { playlistEntity ->
            val playlist = playlistDbConvertor.map(playlistEntity) // Конвертация PlaylistEntity в Playlist
            val tracks = playlistTrackDao.getTracksByPlaylist(playlist.id) // Получение треков для текущего плейлиста
            val trackList = tracks.map { trackDbConvertor.mapFromPlaylistTrackEntity(it) } // Конвертация треков
            playlist.copy(trackList = trackList) // Добавление треков в плейлист
        }
        emit(playlistsWithTracks) // Отправка результата
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long)  {
        val playlistTrackEntity = trackDbConvertor.mapToPlaylistTrackEntity(track, playlistId)
        playlistTrackDao.insertTrackToPlaylist(playlistTrackEntity)
    }

    override suspend fun removeTrackFromPlaylist(track: Int, playlistId: Long) {
        val trackEntity = playlistTrackDao.getTrackInPlaylist(track, playlistId)
        if (trackEntity != null) {
            playlistTrackDao.removeTrackFromPlaylist(trackEntity)
        }
    }

    override suspend fun clearPlaylist(playlistId: Long) {
        playlistTrackDao.clearPlaylist(playlistId)
    }

    private suspend fun addTracksByPlaylistId(playlists: List<Playlist>): List<Playlist> {
        return playlists.map { playlist: Playlist ->
            playlist.copy(trackList = convertFromTrackEntity(playlistTrackDao.getTracksByPlaylist(playlist.id)))
        }
    }

    private fun convertFromTrackEntity(tracks: List<PlaylistTrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.mapFromPlaylistTrackEntity(track) }
    }
}