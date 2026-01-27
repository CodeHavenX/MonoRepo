package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.server.service.models.TimeCardEvent
import kotlin.time.Instant

/**
 * Interface for interacting with the time card database.
 */
interface TimeCardDatastore {

    /**
     * Creates a new time card event. Returns the [Result] of the operation with the created [TimeCardEvent].
     */
    suspend fun createTimeCardEvent(
        employeeId: EmployeeId,
        fallbackEmployeeName: String?,
        propertyId: PropertyId,
        type: TimeCardEventType,
        imageUrl: String?,
        timestamp: Instant,
    ): Result<TimeCardEvent>

    /**
     * Retrieves a time card event by its ID. Returns the [Result] of the operation with the fetched [TimeCardEvent] if found.
     */
    suspend fun getTimeCardEvent(id: TimeCardEventId): Result<TimeCardEvent?>

    /**
     * Retrieves all time card events for an employee member. Returns the [Result] of the operation with a list of [TimeCardEvent].
     */
    suspend fun getTimeCardEvents(employeeId: EmployeeId?): Result<List<TimeCardEvent>>

    /**
     * Deletes a time card event by its ID. Returns the [Result] of the operation.
     */
    suspend fun deleteTimeCardEvent(id: TimeCardEventId): Result<Boolean>

    /**
     * Permanently deletes a soft-deleted time card event record by ID.
     * Only purges if the record is already soft-deleted.
     * This is intended for testing and maintenance purposes only.
     * Returns the [Result] of the operation with a [Boolean] indicating if the record was purged.
     */
    suspend fun purgeTimeCardEvent(id: TimeCardEventId): Result<Boolean>
}
