package com.makiev.playlistmaker.search.data

import com.makiev.playlistmaker.search.data.dto.Response

interface NetworkClient {
    suspend fun sendRequest(dto: Any): Response
}