package com.example.playlistmaker.data.network

import com.example.playlistmaker.ITunesResponse
import com.example.playlistmaker.data.dto.TrackSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search?entity=song ")
    fun search(@Query("term") text: String): Call<ITunesResponse> //не забыть поменять ITunesResponse на TrackSearchResponse
}