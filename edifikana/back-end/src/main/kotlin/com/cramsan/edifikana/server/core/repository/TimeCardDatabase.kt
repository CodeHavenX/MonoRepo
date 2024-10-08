package com.cramsan.edifikana.server.core.repository

import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.requests.CreateTimeCardEventRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventListRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventRequest

/**
 * Interface for interacting with the time card database.
 */
interface TimeCardDatabase {

    /**
     * Creates a new time card event for the given [request]. Returns the [Result] of the operation with the created [TimeCardEvent].
     */
    suspend fun createTimeCardEvent(
        request: CreateTimeCardEventRequest,
    ): Result<TimeCardEvent>

    /**
     * Retrieves a time card event for the given [request]. Returns the [Result] of the operation with the fetched [TimeCardEvent] if found.
     */
    suspend fun getTimeCardEvent(
        request: GetTimeCardEventRequest,
    ): Result<TimeCardEvent?>

    /**
     * Retrieves all time card events for the given [request]. Returns the [Result] of the operation with a list of [TimeCardEvent].
     */
    suspend fun getTimeCardEvents(
        request: GetTimeCardEventListRequest,
    ): Result<List<TimeCardEvent>>
}
