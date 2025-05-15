package com.makiev.playlistmaker.sharing.data

import com.makiev.playlistmaker.settings.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(link: String)
    fun openEmail(emailData: EmailData)
}