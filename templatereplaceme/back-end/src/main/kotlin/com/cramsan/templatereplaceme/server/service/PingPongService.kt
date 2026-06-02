package com.cramsan.templatereplaceme.server.service

import com.cramsan.architecture.server.settings.SettingsHolder
import com.cramsan.framework.annotations.BackendService
import com.cramsan.framework.logging.logD
import com.cramsan.templatereplaceme.server.datastore.PingPongDatastore
import com.cramsan.templatereplaceme.server.service.models.Pong
import com.cramsan.templatereplaceme.server.settings.TemplateReplaceMeSettingKey

/**
 * Example of PingPong service .
 */
@BackendService
class PingPongService(
    private val pingPongDatastore: PingPongDatastore,
    private val settingsHolder: SettingsHolder,
) {
    /**
     * Ping operation that responds with a pong.
     *
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @return A [Result] containing the [Pong] or an error if the operation failed.
     */
    suspend fun ping(
        firstName: String,
        lastName: String,
    ): Result<Pong> {
        logD(TAG, "ping")
        val result =
            pingPongDatastore.ping(
                firstName,
                lastName,
            )
        if (result.isSuccess && settingsHolder.getBoolean(TemplateReplaceMeSettingKey.LogPingPong) == true) {
            logD(TAG, "Pong created successfully: ${result.getOrThrow().id.id}")
        }
        return result
    }

    companion object {
        private const val TAG = "PingPongService"
    }
}
