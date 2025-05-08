package com.example.playlistmaker.library.data.playlists

import com.example.playlistmaker.database.convertors.EmptyPlaylistDbConvertor
import com.example.playlistmaker.database.convertors.TrackDbConvertor
import com.example.playlistmaker.database.dao.PlaylistDao
import com.example.playlistmaker.database.dao.PlaylistTrackDao
import com.example.playlistmaker.library.domain.playlists.api.PlaylistsRepository
import com.example.playlistmaker.createandeditplaylist.domain.models.Playlist
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
    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylistById(playlistId)
    }

    override suspend fun getPlaylistsAll(): Flow<List<Playlist>> {
        return flow {
            val playlistsDb = playlistDao.getAllPlaylists()
            val playlistsWithTracks = playlistsDb.map { playlistEntity ->
                val playlist =
                    playlistDbConvertor.map(playlistEntity)
                val tracks =
                    playlistTrackDao.getTracksByPlaylist(playlist.id)
                val trackList =
                    tracks.map { trackDbConvertor.mapFromPlaylistTrackEntity(it) }
                playlist.copy(trackList = trackList)
            }
            emit(playlistsWithTracks)
        }
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlistId: Long
    ): Flow<Resource<String>> = flow {
        val existingTrack = playlistTrackDao.getTrackInPlaylist(track.trackId, playlistId)
        val playlist = playlistDbConvertor.map(playlistDao.getPlaylistById(playlistId))
        if (existingTrack != null) {
            emit(Resource.Error("Track '${track.trackName}' is already in the playlist."))
        } else {
            val playlistTrackEntity = trackDbConvertor.mapToPlaylistTrackEntity(track, playlistId)
            playlistTrackDao.insertTrackToPlaylist(playlistTrackEntity)
            emit(Resource.Success("Track '${track.trackName}' was added successfully to the playlist '${playlist.name}'"))
        }
    }

    override suspend fun removeTrackFromPlaylist(trackId: Int, playlistId: Long) {
        val trackEntity = playlistTrackDao.getTrackInPlaylist(trackId, playlistId)
        if (trackEntity != null) {
            playlistTrackDao.removeTrackFromPlaylist(trackEntity)
        }

        val isOrphan = playlistTrackDao.getPlaylistsForTrack(trackId).isEmpty()
        if (isOrphan) {
            playlistTrackDao.deleteTrackById(trackId)
        }
    }

    override suspend fun clearPlaylist(playlistId: Long) {
        playlistTrackDao.clearPlaylist(playlistId)
    }
}