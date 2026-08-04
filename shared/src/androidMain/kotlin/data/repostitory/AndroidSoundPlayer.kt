package data.repostitory

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

class AndroidSoundPlayer(val appContext: Context) {
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