package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.playlistmaker.settings.domain.model.EmailData
import com.example.playlistmaker.sharing.data.ExternalNavigator

class ExternalNavigatorImpl (private val context: Context) : ExternalNavigator {
    override fun shareLink(link: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }
        val createChooserIntent = Intent.createChooser(intent, null)
        context.startActivity(createChooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun openEmail(emailData: EmailData) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.emailList))
            putExtra(Intent.EXTRA_SUBJECT, emailData.messageSubject)
            putExtra(Intent.EXTRA_TEXT, emailData.message)
        }
        try {
            context.startActivity(intent)
        } catch (e:Exception){
            Toast.makeText(context, emailData.errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}