package com.example.playlistmaker.library.data.playlists

import com.example.playlistmaker.database.convertors.EmptyPlaylistDbConvertor
import com.example.playlistmaker.database.convertors.TrackDbConvertor
import com.example.playlistmaker.database.dao.PlaylistDao
import com.example.playlistmaker.database.dao.PlaylistTrackDao
import com.example.playlistmaker.library.domain.playlists.api.PlaylistsRepository
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
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
        emit(playlistsWithTracks)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long): Flow<Resource<String>>  = flow {
        // Проверяем, есть ли трек в плейлисте
        val existingTrack = playlistTrackDao.getTrackInPlaylist(track.trackId, playlistId)
        val playlist = playlistDbConvertor.map(playlistDao.getPlaylistById(playlistId))
        if (existingTrack != null) {
            emit(Resource.Error("Track '${track.trackName}' is already in the playlist."))
        } else {
            // Конвертируем трек в PlaylistTrackEntity и добавляем в базу данных
            val playlistTrackEntity = trackDbConvertor.mapToPlaylistTrackEntity(track, playlistId)
            playlistTrackDao.insertTrackToPlaylist(playlistTrackEntity)
            emit(Resource.Success("Track '${track.trackName}' was added successfully to the playlist '${playlist.name}'"))
        }
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
}