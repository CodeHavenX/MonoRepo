package com.codehavenx.platform.bot.service

import com.cramsan.framework.logging.logI
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.readBytes

/**
 * Service to generate speech data.
 */
class TextToSpeechService(
    private val httpClient: HttpClient,
    private val port: String,
) {

    /**
     * Generate a [ByteArray] representing an audio file from the provided [message].
     */
    suspend fun generateSpeech(message: String): ByteArray {
        logI(TAG, "Generating speech from message of size ${message.length}")
        val response = httpClient.post("http://192.168.1.151:$port/tts") {
            setBody(message)
        }
        return response.readBytes()
    }

    companion object {
        private const val TAG = "TranslationService"
    }
}
