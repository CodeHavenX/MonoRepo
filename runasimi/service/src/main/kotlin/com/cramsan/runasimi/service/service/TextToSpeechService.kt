package com.cramsan.runasimi.service.service

import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.readBytes
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Service to generate speech data.
 */
class TextToSpeechService(
    private val httpClient: HttpClient,
    private val endponit: String,
    private val dataCachingService: DataCachingService,
    private val discordCommunicationService: DiscordCommunicationService,
) {

    private val mutex = Mutex()

    /**
     * Generate a [ByteArray] representing an audio file from the provided [message].
     */
    suspend fun generateSpeech(message: String, lang: String): ByteArray? {
        mutex.withLock {
            val cacheKey = generateKey(message, lang)
            return if (dataCachingService.contains(cacheKey)) {
                logI(TAG, "Voice data found in cache.")
                val data = dataCachingService.get(cacheKey)

                discordCommunicationService.sendMessage("Request from cache: $message")
                if (data == null || data.isEmpty()) {
                    logE(TAG, "Voice data from cache is invalid.")
                    null
                } else {
                    data
                }
            } else {
                logI(TAG, "Generating speech from message of size ${message.length}")
                discordCommunicationService.sendMessage("Request from service: $message")
                val response = httpClient.post(endponit) {
                    parameter("lang", lang)
                    setBody(message)
                }
                val data = response.readBytes()

                if (response.status == HttpStatusCode.OK) {
                    dataCachingService.put(cacheKey, data)
                    data
                } else {
                    null
                }
            }
        }
    }

    private fun generateKey(message: String, lang: String): String {
        return "${message}_$lang"
    }

    companion object {
        private const val TAG = "TranslationService"
    }
}
