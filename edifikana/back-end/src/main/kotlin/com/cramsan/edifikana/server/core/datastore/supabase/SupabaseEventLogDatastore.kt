package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.server.core.datastore.EventLogDatastore
import com.cramsan.edifikana.server.core.datastore.supabase.models.EventLogEntryEntity
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.requests.CreateEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateEventLogEntryRequest
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest

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
        request: CreateEventLogEntryRequest,
    ): Result<EventLogEntry> = runSuspendCatching(TAG) {
        logD(TAG, "Creating event log entry: %s", request.title)
        val requestEntity: EventLogEntryEntity.CreateEventLogEntryEntity = request.toEventLogEntryEntity()

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
        request: GetEventLogEntryRequest,
    ): Result<EventLogEntry?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting event log entry: %s", request.id)

        val eventLogEntryEntity = postgrest.from(EventLogEntryEntity.COLLECTION).select {
            filter {
                EventLogEntryEntity::id eq request.id.eventLogEntryId
            }
        }.decodeSingleOrNull<EventLogEntryEntity>()

        eventLogEntryEntity?.toEventLogEntry()
    }

    @OptIn(SupabaseModel::class)
    override suspend fun getEventLogEntries(): Result<List<EventLogEntry>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all event log entries")

        postgrest.from(EventLogEntryEntity.COLLECTION).select {
            select()
        }.decodeList<EventLogEntryEntity>().map { it.toEventLogEntry() }
    }

    /**
     * Updates an event log entry with the given [request]. Returns the [Result] of the operation with the updated [EventLogEntry].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun updateEventLogEntry(
        request: UpdateEventLogEntryRequest,
    ): Result<EventLogEntry> = runSuspendCatching(TAG) {
        logD(TAG, "Updating event log entry: %s", request.id)

        postgrest.from(EventLogEntryEntity.COLLECTION).update(
            {
                request.type?.let { value -> EventLogEntryEntity::type setTo value }
                request.fallbackEventType?.let { value -> EventLogEntryEntity::fallbackEventType setTo value }
                request.title?.let { value -> EventLogEntryEntity::title setTo value }
                request.description?.let { value -> EventLogEntryEntity::description setTo value }
                request.unit?.let { value -> EventLogEntryEntity::unit setTo value }
            }
        ) {
            select()
            filter {
                EventLogEntryEntity::id eq request.id
            }
        }.decodeSingle<EventLogEntryEntity>().toEventLogEntry()
    }

    /**
     * Deletes an event log entry with the given [request]. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteEventLogEntry(
        request: DeleteEventLogEntryRequest,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Deleting event log entry: %s", request.id)

        postgrest.from(EventLogEntryEntity.COLLECTION).delete {
            select()
            filter {
                EventLogEntryEntity::id eq request.id.eventLogEntryId
            }
        }.decodeSingleOrNull<EventLogEntryEntity>() != null
    }

    companion object {
        const val TAG = "SupabaseEventLogDatastore"
    }
}
