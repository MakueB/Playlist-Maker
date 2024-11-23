package com.example.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.example.playlistmaker.database.AppDatabase
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.ITunesApi
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.storage.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.ui.ITUNES_BASE_URL
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.utils.Keys
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

val dataModule = module {
    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
            .create()
    }

    single <OkHttpClient> {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient().newBuilder().addInterceptor(interceptor)
            .build()
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    single {
        androidContext().getSharedPreferences(Keys.PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    factory {
        Gson()
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get(), get())
    }

    single<NetworkClient>{
        RetrofitNetworkClient(get(), get())
    }

    single <ExternalNavigator> {
        ExternalNavigatorImpl(get())
    }

    single {
        get<AppDatabase>().trackDao()
    }

    single {
        get<AppDatabase>().playlistDao()
    }

    single {
        get<AppDatabase>().playlistTrackDao()
    }
}