package com.example.playlistmaker.sharing.data

import com.example.playlistmaker.settings.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(link: String)
    fun openEmail(emailData: EmailData)
}