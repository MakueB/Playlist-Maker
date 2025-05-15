package com.makiev.playlistmaker.player.domain.impl

import com.makiev.playlistmaker.player.domain.api.PlayerInteractor
import com.makiev.playlistmaker.player.domain.api.PlayerRepository
import com.makiev.playlistmaker.search.domain.models.Track

class PlayerInteractorImpl(private val playerRepository: PlayerRepository) : PlayerInteractor {
    override fun preparePlayer(track: Track?, onPrepared:() -> Unit, onComplete: () -> Unit) {
        playerRepository.preparePlayer(track, onPrepared, onComplete)
    }

    override fun startPlayer() {
        playerRepository.startPlayer()
    }

    override fun pausePlayer() {
        playerRepository.pausePlayer()
    }

    override fun getCurrentPosition(): Long {
        return playerRepository.getCurrentPosition()
    }

    override fun release() {
        playerRepository.release()
    }

    override fun reset() {
        playerRepository.reset()
    }

}