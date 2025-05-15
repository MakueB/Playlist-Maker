package com.makiev.playlistmaker.sharing.data.impl

import android.content.Context
import com.makiev.playlistmaker.R
import com.makiev.playlistmaker.sharing.domain.api.SharingRepository
import com.makiev.playlistmaker.settings.domain.model.EmailData
import com.makiev.playlistmaker.sharing.data.ExternalNavigator

class SharingRepositoryImpl(private val context: Context, private val externalNavigator: ExternalNavigator) :
    SharingRepository {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.android_developer_course)
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.oferta_yandex_url)
    }

    override fun openSupport() {
        val emailData = EmailData(
            arrayOf(context.getString(R.string.support_email)),
            context.getString(R.string.support_message_subject),
            context.getString(R.string.support_message),
            context.getString(R.string.error_message)
        )
        externalNavigator.openEmail(emailData)
    }
}