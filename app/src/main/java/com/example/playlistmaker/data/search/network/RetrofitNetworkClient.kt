//package com.example.playlistmaker.data.search.network//package com.example.playlistmaker.data.network
//
//import com.example.playlistmaker.data.search.NetworkClient
//import com.example.playlistmaker.data.search.dto.Response
//import com.example.playlistmaker.data.search.dto.TrackSearchRequest
//import com.example.playlistmaker.ui.search.ITUNES_BASE_URL
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//class RetrofitNetworkClient : NetworkClient {
//
//    private val retrofit = Retrofit.Builder()
//        .baseUrl(ITUNES_BASE_URL)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    private val iTunesService = retrofit.create(ITunesApi::class.java)
//    override fun sendRequest(dto: Any): Response {
//        return if (dto is TrackSearchRequest) {
//            val response = iTunesService.search(dto.query).execute()
//
//            val body: Response = response.body() ?: Response()
//
//            body.apply { responseCode = response.code() }
//        } else {
//            Response().apply { responseCode = 400 }
//        }
//    }
//}
