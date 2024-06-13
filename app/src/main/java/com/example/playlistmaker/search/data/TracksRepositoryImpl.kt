package com.example.playlistmaker.search.data

import com.example.playlistmaker.database.AppDatabase
import com.example.playlistmaker.database.dao.TrackDbConvertor
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase,
    private val convertor: TrackDbConvertor
    ) : TracksRepository {
    override fun search(query: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.sendRequest(TrackSearchRequest(query))
        when (response.responseCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            200 -> {
                with(response as TrackSearchResponse) {
                    val favorites = appDatabase.trackDao().getIdAll()
                    val data = results.map {
                        val isFavorite = favorites.contains(it.trackId)
                        Track(
                            it.trackId,
                            it.trackName,
                            it.artistName,
                            it.getTimeInMmSs(),
                            it.getCoverArtwork(),
                            it.collectionName,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl,
                            isFavorite
                        )
                    }
                    emit(Resource.Success(data))
                }
            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}
