package com.exampl3.flashlight.Domain.useCase

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundPlayer @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    private var mediaPlayer: MediaPlayer? = null

    fun playSound(uri: Uri) {
        stop()

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(appContext, uri)
                setOnPreparedListener {
                    try {
                        start()
                    } catch (e: IllegalStateException) {
                    }
                }
                setOnCompletionListener {
                    safeRelease()
                }
                setOnErrorListener { mp, what, extra ->
                    safeRelease()
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            safeRelease()
        }
    }

    fun stop() {
        safeRelease()
    }

    private fun safeRelease() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
        } catch (e: IllegalStateException) {
        } finally {
            mediaPlayer = null
        }
    }
}