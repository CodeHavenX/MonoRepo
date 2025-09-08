package com.cramsan.edifikana.server.core.datastore

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import kotlin.time.Instant

/**
 * Interface for interacting with the time card database.
 */
interface TimeCardDatastore {

    /**
     * Creates a new time card event. Returns the [Result] of the operation with the created [TimeCardEvent].
     */
    suspend fun createTimeCardEvent(
        staffId: StaffId,
        fallbackStaffName: String?,
        propertyId: PropertyId,
        type: TimeCardEventType,
        imageUrl: String?,
        timestamp: Instant,
    ): Result<TimeCardEvent>

    /**
     * Retrieves a time card event by its ID. Returns the [Result] of the operation with the fetched [TimeCardEvent] if found.
     */
    suspend fun getTimeCardEvent(
        id: TimeCardEventId,
    ): Result<TimeCardEvent?>

    /**
     * Retrieves all time card events for a staff member. Returns the [Result] of the operation with a list of [TimeCardEvent].
     */
    suspend fun getTimeCardEvents(
        staffId: StaffId?,
    ): Result<List<TimeCardEvent>>

    /**
     * Deletes a time card event by its ID. Returns the [Result] of the operation.
     */
    suspend fun deleteTimeCardEvent(
        id: TimeCardEventId,
    ): Result<Boolean>
}
