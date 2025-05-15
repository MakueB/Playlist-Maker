package com.makiev.playlistmaker.player.domain.api

import com.makiev.playlistmaker.search.domain.models.Track

interface PlayerInteractor {
    fun preparePlayer(
        track: Track?,
        onPrepared: () -> Unit,
        onComplete: () -> Unit
    )

    fun startPlayer()
    fun pausePlayer()
    fun getCurrentPosition(): Long
    fun release()
    fun reset()
}