package com.makiev.playlistmaker.search.data

import com.makiev.playlistmaker.database.AppDatabase
import com.makiev.playlistmaker.search.data.dto.TrackSearchRequest
import com.makiev.playlistmaker.search.data.dto.TrackSearchResponse
import com.makiev.playlistmaker.search.domain.api.TracksRepository
import com.makiev.playlistmaker.search.domain.models.Track
import com.makiev.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase,
    ) : TracksRepository {
    override fun search(query: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.sendRequest(TrackSearchRequest(query))
        when (response.responseCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            404 -> {
                emit(Resource.Error("Ничего не нашлось"))
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
