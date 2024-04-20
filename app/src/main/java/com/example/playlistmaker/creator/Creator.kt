package com.example.playlistmaker
import android.content.Context
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(context: Context) : TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }
    fun provideTracksInteractor(context: Context) : TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }
}