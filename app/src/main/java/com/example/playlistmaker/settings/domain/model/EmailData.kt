package com.example.playlistmaker.settings.domain.model

data class EmailData (
    val emailList: Array<String>,
    val messageSubject: String,
    val message: String,
    val errorMessage: String
)