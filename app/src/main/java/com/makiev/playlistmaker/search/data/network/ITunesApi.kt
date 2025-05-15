package com.makiev.playlistmaker.search.data.network

import com.makiev.playlistmaker.search.data.dto.TrackSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search?entity=song ")
    suspend fun search(@Query("term") text: String): TrackSearchResponse
}