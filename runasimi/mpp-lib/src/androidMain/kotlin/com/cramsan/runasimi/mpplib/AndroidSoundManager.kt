package com.cramsan.runasimi.mpplib

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.cramsan.framework.logging.logE
import com.cramsan.runasimi.mpplib.main.SoundManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AndroidSoundManager(
    private val scope: CoroutineScope,
) : SoundManager {

    override fun playSoundAsync(path: String) {
        scope.launch { playSound(path) }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun playSound(path: String) {
        val mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.reset()
            delay(100)
            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build(),
            )
            mediaPlayer.setDataSource(path)
            mediaPlayer.prepare() // might take long! (for buffering, etc)

            val duration = mediaPlayer.duration
            mediaPlayer.start()
            delay((duration + DURATION_PADDING_MS).toLong())
        } catch (e: Exception) {
            logE(TAG, "Error while playing audio: $path", e)
        } finally {
            mediaPlayer.release()
        }
    }

    companion object {
        private const val TAG = "AndroidSoundManager"
        private const val DURATION_PADDING_MS = 200
    }
}
