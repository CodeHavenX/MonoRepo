package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.server.datastore.EventLogDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.EventLogEntryEntity
import com.cramsan.edifikana.server.service.models.EventLogEntry
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.time.Instant

/**
 * Datastore for managing event log entries.
 */
class SupabaseEventLogDatastore(
    private val postgrest: Postgrest,
) : EventLogDatastore {

    /**
     * Creates a new event log entry for the given [request]. Returns the [Result] of the operation with the created [EventLogEntry].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createEventLogEntry(
        employeeId: EmployeeId?,
        fallbackEmployeeName: String?,
        propertyId: PropertyId,
        type: EventLogEventType,
        fallbackEventType: String?,
        timestamp: Instant,
        title: String,
        description: String?,
        unit: String,
    ): Result<EventLogEntry> = runSuspendCatching(TAG) {
        logD(TAG, "Creating event log entry: %s", title)
        val requestEntity: EventLogEntryEntity.CreateEventLogEntryEntity = CreateEventLogEntryEntity(
            employeeId = employeeId,
            fallbackEmployeeName = fallbackEmployeeName,
            propertyId = propertyId,
            type = type,
            fallbackEventType = fallbackEventType,
            timestamp = timestamp,
            title = title,
            description = description,
            unit = unit
        )

        val createdEventLogEntry = postgrest.from(EventLogEntryEntity.COLLECTION).insert(requestEntity) {
            select()
        }.decodeSingle<EventLogEntryEntity>()
        logD(TAG, "Event log entry created eventId: %s", createdEventLogEntry.id)
        createdEventLogEntry.toEventLogEntry()
    }

    /**
     * Retrieves an event log entry for the given [request]. Returns the [Result] of the operation with the fetched [EventLogEntry] if found.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getEventLogEntry(
        id: EventLogEntryId,
    ): Result<EventLogEntry?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting event log entry: %s", id)

        val eventLogEntryEntity = postgrest.from(EventLogEntryEntity.COLLECTION).select {
            filter {
                EventLogEntryEntity::id eq id.eventLogEntryId
            }
        }.decodeSingleOrNull<EventLogEntryEntity>()

        eventLogEntryEntity?.toEventLogEntry()
    }

    @OptIn(SupabaseModel::class)
    override suspend fun getEventLogEntries(
        propertyId: PropertyId,
    ): Result<List<EventLogEntry>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all event log entries")

        postgrest.from(EventLogEntryEntity.COLLECTION).select {
            filter {
                EventLogEntryEntity::propertyId eq propertyId.propertyId
            }
        }.decodeList<EventLogEntryEntity>().map { it.toEventLogEntry() }
    }

    /**
     * Updates an event log entry with the given [request]. Returns the [Result] of the operation with the updated [EventLogEntry].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun updateEventLogEntry(
        id: EventLogEntryId,
        type: EventLogEventType?,
        fallbackEventType: String?,
        title: String?,
        description: String?,
        unit: String?,
    ): Result<EventLogEntry> = runSuspendCatching(TAG) {
        logD(TAG, "Updating event log entry: %s", id)

        postgrest.from(EventLogEntryEntity.COLLECTION).update(
            {
                type?.let { value -> EventLogEntryEntity::type setTo value }
                fallbackEventType?.let { value -> EventLogEntryEntity::fallbackEventType setTo value }
                title?.let { value -> EventLogEntryEntity::title setTo value }
                description?.let { value -> EventLogEntryEntity::description setTo value }
                unit?.let { value -> EventLogEntryEntity::unit setTo value }
            }
        ) {
            select()
            filter {
                EventLogEntryEntity::id eq id.eventLogEntryId
            }
        }.decodeSingle<EventLogEntryEntity>().toEventLogEntry()
    }

    /**
     * Deletes an event log entry with the given [request]. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteEventLogEntry(
        id: EventLogEntryId,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Deleting event log entry: %s", id)

        postgrest.from(EventLogEntryEntity.COLLECTION).delete {
            select()
            filter {
                EventLogEntryEntity::id eq id.eventLogEntryId
            }
        }.decodeSingleOrNull<EventLogEntryEntity>() != null
    }

    companion object {
        const val TAG = "SupabaseEventLogDatastore"
    }
}
