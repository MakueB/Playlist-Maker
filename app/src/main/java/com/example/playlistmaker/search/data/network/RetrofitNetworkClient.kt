package com.example.playlistmaker.search.data.network//package com.example.playlistmaker.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest

class RetrofitNetworkClient(private val context: Context, private val iTunesService: ITunesApi) : NetworkClient {
    override fun sendRequest(dto: Any): Response {
        if (isConnected() == false) {
            return Response().apply { responseCode = -1 }
        }

        return if (dto is TrackSearchRequest) {
            val response = iTunesService.search(dto.query).execute()

            val body: Response = response.body() ?: Response()

            body.apply { responseCode = response.code() }
        } else {
            Response().apply { responseCode = 400 }
        }
    }

    private fun isConnected(): Boolean{
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}
