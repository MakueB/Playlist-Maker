package com.makiev.playlistmaker.di

import com.makiev.playlistmaker.createandeditplaylist.data.CreatePlaylistRepositoryImpl
import com.makiev.playlistmaker.createandeditplaylist.data.ImageStorageRepositoryImpl
import com.makiev.playlistmaker.createandeditplaylist.domain.api.CreatePlaylistRepository
import com.makiev.playlistmaker.createandeditplaylist.domain.api.ImageStorageRepository
import com.makiev.playlistmaker.database.convertors.EmptyPlaylistDbConvertor
import com.makiev.playlistmaker.database.convertors.TrackDbConvertor
import com.makiev.playlistmaker.details.data.DetailsRepositoryImpl
import com.makiev.playlistmaker.details.domain.api.DetailsRepository
import com.makiev.playlistmaker.library.data.favorites.FavoritesRepositoryImpl
import com.makiev.playlistmaker.library.data.playlists.PlaylistsRepositoryImpl
import com.makiev.playlistmaker.library.domain.favorites.api.FavoritesRepository
import com.makiev.playlistmaker.library.domain.playlists.api.PlaylistsRepository
import com.makiev.playlistmaker.player.data.PlayerRepositoryImpl
import com.makiev.playlistmaker.player.domain.api.PlayerRepository
import com.makiev.playlistmaker.search.data.TracksRepositoryImpl
import com.makiev.playlistmaker.search.data.storage.SearchHistoryRepositoryImpl
import com.makiev.playlistmaker.search.domain.api.SearchHistoryRepository
import com.makiev.playlistmaker.search.domain.api.TracksRepository
import com.makiev.playlistmaker.settings.data.SettingsRepositoryImpl
import com.makiev.playlistmaker.settings.domain.api.SettingsRepository
import com.makiev.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import com.makiev.playlistmaker.sharing.domain.api.SharingRepository
import org.koin.android.ext.koin.androidContext
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

    single<ImageStorageRepository> {
        ImageStorageRepositoryImpl(androidContext())
    }
}