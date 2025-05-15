package com.makiev.playlistmaker.player.data

import android.media.MediaPlayer
import com.makiev.playlistmaker.player.domain.api.PlayerRepository
import com.makiev.playlistmaker.search.domain.models.Track

class PlayerRepositoryImpl: PlayerRepository {
    private val mediaPlayer: MediaPlayer = MediaPlayer()

    override fun preparePlayer(track: Track?, onPrepared: () -> Unit, onComplete: () -> Unit) {
        mediaPlayer.apply {
            reset()
            setDataSource(track?.previewUrl)
            prepareAsync()
            setOnPreparedListener{
                onPrepared.invoke()
            }
            setOnCompletionListener {
                onComplete.invoke()
            }
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun getCurrentPosition(): Long {
        return mediaPlayer.currentPosition.toLong()
    }

    override fun reset() {
        mediaPlayer.reset()
    }

    override fun release() {
        mediaPlayer.release()
    }
}