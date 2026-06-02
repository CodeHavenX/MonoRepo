package com.cramsan.templatereplaceme.server.datastore

import com.cramsan.framework.annotations.BackendDatastore
import com.cramsan.templatereplaceme.server.service.models.Pong

/**
 * Interface defining ping-pong data operations.
 */
@BackendDatastore
interface PingPongDatastore {
    /**
     * Creates a pong response with the given first and last name.
     *
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @return A [Result] containing the created [Pong] or an error if the operation failed.
     */
    suspend fun ping(
        firstName: String,
        lastName: String,
    ): Result<Pong>
}
