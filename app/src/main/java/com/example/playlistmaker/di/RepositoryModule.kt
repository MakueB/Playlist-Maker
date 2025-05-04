package com.example.playlistmaker.di

import com.example.playlistmaker.database.convertors.EmptyPlaylistDbConvertor
import com.example.playlistmaker.database.convertors.TrackDbConvertor
import com.example.playlistmaker.library.data.favorites.FavoritesRepositoryImpl
import com.example.playlistmaker.library.data.playlists.PlaylistsRepositoryImpl
import com.example.playlistmaker.library.domain.favorites.api.FavoritesRepository
import com.example.playlistmaker.library.domain.playlists.api.PlaylistsRepository
import com.example.playlistmaker.createandeditplaylist.data.CreatePlaylistRepositoryImpl
import com.example.playlistmaker.createandeditplaylist.domain.api.CreatePlaylistRepository
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.details.data.DetailsRepositoryImpl
import com.example.playlistmaker.details.domain.api.DetailsRepository
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
    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<SharingRepository> {
        SharingRepositoryImpl(get(), get())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl()
    }

    factory {
        TrackDbConvertor()
    }

    factory {
        EmptyPlaylistDbConvertor()
    }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    single<CreatePlaylistRepository> {
        CreatePlaylistRepositoryImpl(get(), get())
    }

    single <PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get(), get(), get())
    }

    single <DetailsRepository> {
        DetailsRepositoryImpl()
    }
}