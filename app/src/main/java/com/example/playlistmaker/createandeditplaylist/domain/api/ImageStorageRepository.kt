package com.example.playlistmaker.createandeditplaylist.domain.api

import android.content.ContentResolver
import android.net.Uri

interface ImageStorageRepository {
    suspend fun saveToPrivateStorage(uri: Uri, contentResolver: ContentResolver): Uri
}