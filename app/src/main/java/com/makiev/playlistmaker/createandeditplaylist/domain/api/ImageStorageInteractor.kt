package com.makiev.playlistmaker.createandeditplaylist.domain.api

import android.content.ContentResolver
import android.net.Uri

interface ImageStorageInteractor {
    suspend fun saveToPrivateStorage(uri: Uri, contentResolver: ContentResolver): Uri
}