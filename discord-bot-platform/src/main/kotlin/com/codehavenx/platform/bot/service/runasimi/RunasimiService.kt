package com.codehavenx.platform.bot.service.runasimi

import com.cramsan.framework.logging.logW
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.readBytes
import io.ktor.http.HttpStatusCode

/**
 * Service to interact with the Runasimi API.
 */
class RunasimiService(
    private val httpClient: HttpClient,
    private val endpoint: String,
) {

    /**
     * Fetches an audio file from the Runasimi API. The content of the message is the [statement] with a
     * [variant] as a three character code.
     *
     * The supported variants are located in https://dl.fbaipublicfiles.com/mms/tts/all-tts-languages.html
     */
    suspend fun fetchAudioFile(statement: String, variant: String): ByteArray? {
        val response = httpClient.post("$endpoint/tts?lang=$variant") {
            setBody(statement)
        }

        if (response.status != HttpStatusCode.OK) {
            logW(TAG, "Audio API request was not successful. Response ${response.status}")
            return null
        }

        return response.readBytes()
    }

    companion object {
        private const val TAG = "RunasimiService"
    }
}
