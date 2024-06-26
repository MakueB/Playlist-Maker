package com.example.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

class RetrofitNetworkClient(
    private val context: Context,
    private val iTunesService: ITunesApi) : NetworkClient {
    override suspend fun sendRequest(dto: Any): Response {
        if (isConnected() == false) {
            return Response().apply { responseCode = -1 }
        }

        if (dto !is TrackSearchRequest) {
            return Response().apply { responseCode = 400 }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = iTunesService.search(dto.query)
                response.apply { responseCode = 200 }
            }
            catch (ex: Exception) {
                if (ex is retrofit2.HttpException)
                    Response().apply { responseCode = 404 }
                else
                    Response().apply { responseCode = 500 }
            }
            catch (e: Throwable) {
                Response().apply { responseCode = 500 }
            }
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
