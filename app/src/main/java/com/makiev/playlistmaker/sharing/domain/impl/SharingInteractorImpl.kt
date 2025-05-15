package com.makiev.playlistmaker.sharing.domain.impl

import com.makiev.playlistmaker.sharing.domain.api.SharingInteractor
import com.makiev.playlistmaker.sharing.domain.api.SharingRepository

class SharingInteractorImpl (private val repository: SharingRepository) : SharingInteractor {
    override fun shareApp() {
        repository.shareApp()
    }

    override fun openTerms() {
        repository.openTerms()
    }

    override fun openSupport() {
        repository.openSupport()
    }
}