package com.cramsan.runasimi.service.service

import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Service to generate speech data.
 */
class TextToSpeechService(
    private val httpClient: HttpClient,
    private val port: String,
    private val dataCachingService: DataCachingService,
) {

    private val mutex = Mutex()

    /**
     * Generate a [ByteArray] representing an audio file from the provided [message].
     */
    suspend fun generateSpeech(message: String): ByteArray {
        mutex.withLock {
            return if (dataCachingService.contains(message)) {
                logI(TAG, "Voice data found in cache.")
                val data = dataCachingService.get(message)

                if (data == null || data.isEmpty()) {
                    logE(TAG, "Voice data from cache is invalid.")
                    byteArrayOf()
                } else {
                    data
                }
            } else {
                logI(TAG, "Generating speech from message of size ${message.length}")
                val response = httpClient.post("http://192.168.1.151:$port/tts") {
                    setBody(message)
                }
                val data = response.readBytes()

                dataCachingService.put(message, data)
                data
            }
        }
    }

    companion object {
        private const val TAG = "TranslationService"
    }
}
