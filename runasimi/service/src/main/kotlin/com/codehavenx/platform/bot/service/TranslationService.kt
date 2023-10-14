package com.codehavenx.platform.bot.service

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.readBytes

class TranslationService(
    private val httpClient: HttpClient,
    private val port: String,
) {
    suspend fun sendMessage(message: String): ByteArray {
        val response = httpClient.post("http://192.168.1.151:$port/tts") {
            setBody(message)
        }
        return response.readBytes()
    }

    companion object {
        private const val TAG = "TranslationService"
    }
}
