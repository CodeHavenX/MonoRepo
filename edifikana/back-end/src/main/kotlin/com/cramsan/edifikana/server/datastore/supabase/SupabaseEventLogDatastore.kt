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
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Datastore for managing event log entries.
 */
class SupabaseEventLogDatastore(private val postgrest: Postgrest, private val clock: Clock) : EventLogDatastore {

    /**
     * Creates a new event log entry for tracking property events.
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
            unit = unit,
        )

        val createdEventLogEntry = postgrest.from(EventLogEntryEntity.COLLECTION).insert(requestEntity) {
            select()
        }.decodeSingle<EventLogEntryEntity>()
        logD(TAG, "Event log entry created eventId: %s", createdEventLogEntry.id)
        createdEventLogEntry.toEventLogEntry()
    }

    /**
     * Retrieves an event log entry by [id]. Returns the [EventLogEntry] if found, null otherwise.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getEventLogEntry(id: EventLogEntryId): Result<EventLogEntry?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting event log entry: %s", id)

        val eventLogEntryEntity = postgrest.from(EventLogEntryEntity.COLLECTION).select {
            filter {
                EventLogEntryEntity::id eq id.eventLogEntryId
                EventLogEntryEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<EventLogEntryEntity>()

        eventLogEntryEntity?.toEventLogEntry()
    }

    /**
     * Gets all event log entries for a [propertyId].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getEventLogEntries(propertyId: PropertyId): Result<List<EventLogEntry>> =
        runSuspendCatching(TAG) {
            logD(TAG, "Getting all event log entries")

            postgrest.from(EventLogEntryEntity.COLLECTION).select {
                filter {
                    EventLogEntryEntity::propertyId eq propertyId.propertyId
                    EventLogEntryEntity::deletedAt isExact null
                }
            }.decodeList<EventLogEntryEntity>().map { it.toEventLogEntry() }
        }

    /**
     * Updates an event log entry's attributes. Only non-null parameters are updated.
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
            },
        ) {
            select()
            filter {
                EventLogEntryEntity::id eq id.eventLogEntryId
            }
        }.decodeSingle<EventLogEntryEntity>().toEventLogEntry()
    }

    /**
     * Soft deletes an event log entry by [id]. Returns true if successful.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteEventLogEntry(id: EventLogEntryId): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Soft deleting event log entry: %s", id)

        postgrest.from(EventLogEntryEntity.COLLECTION).update({
            EventLogEntryEntity::deletedAt setTo clock.now()
        }) {
            select()
            filter {
                EventLogEntryEntity::id eq id.eventLogEntryId
                EventLogEntryEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<EventLogEntryEntity>() != null
    }

    /**
     * Permanently deletes a soft-deleted event log entry by [id]. Returns true if successful.
     * Only purges records that are already soft-deleted (deletedAt is not null).
     */
    @OptIn(SupabaseModel::class)
    override suspend fun purgeEventLogEntry(id: EventLogEntryId): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Purging soft-deleted event log entry: %s", id)

        // First verify the record exists and is soft-deleted
        val entity = postgrest.from(EventLogEntryEntity.COLLECTION).select {
            filter {
                EventLogEntryEntity::id eq id.eventLogEntryId
            }
        }.decodeSingleOrNull<EventLogEntryEntity>()

        // Only purge if it exists and is soft-deleted
        if (entity?.deletedAt == null) {
            return@runSuspendCatching false
        }

        // Delete the record
        postgrest.from(EventLogEntryEntity.COLLECTION).delete {
            filter {
                EventLogEntryEntity::id eq id.eventLogEntryId
            }
        }
        true
    }

    companion object {
        const val TAG = "SupabaseEventLogDatastore"
    }
}
