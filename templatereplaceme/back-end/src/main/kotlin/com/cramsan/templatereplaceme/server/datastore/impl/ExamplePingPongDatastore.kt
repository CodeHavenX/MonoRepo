package com.cramsan.templatereplaceme.server.datastore.impl

import com.cramsan.framework.annotations.BackendDatastore
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.utils.uuid.UUID
import com.cramsan.templatereplaceme.lib.model.PingPong
import com.cramsan.templatereplaceme.server.datastore.PingPongDatastore
import com.cramsan.templatereplaceme.server.service.models.Pong
import kotlinx.coroutines.delay

/**
 * Implementation of [PingPongDatastore] that provides ping-pong data operations.
 */
@BackendDatastore
class ExamplePingPongDatastore : PingPongDatastore {
    override suspend fun ping(
        firstName: String,
        lastName: String,
    ): Result<Pong> =
        runSuspendCatching(TAG) {
            @Suppress("MagicNumber")
            delay(2000) // Simulate network/database delay
            Pong(
                id = PingPong(UUID.random()),
                firstName = firstName,
                lastName = lastName,
            )
        }

    companion object {
        private const val TAG = "ExamplePingPongDatastore"
    }
}
