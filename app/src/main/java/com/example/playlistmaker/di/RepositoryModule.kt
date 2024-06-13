package com.example.playlistmaker.di

import com.example.playlistmaker.database.dao.TrackDbConvertor
import com.example.playlistmaker.library.data.FavoritesRepositoryImpl
import com.example.playlistmaker.library.domain.api.FavoritesRepository
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.storage.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.api.SharingRepository
import org.koin.dsl.module

val repositoryModule = module {
    single <TracksRepository>{
        TracksRepositoryImpl(get(), get(), get())
    }

    single <SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<SharingRepository> {
        SharingRepositoryImpl(get(), get())
    }

    factory  <PlayerRepository> {
        PlayerRepositoryImpl()
    }

    factory { TrackDbConvertor() }

    single <FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }
}