package com.codehavenx.alpaca.backend.core.repository.supabase

import com.codehavenx.alpaca.backend.core.repository.CalendarDatabase
import com.codehavenx.alpaca.backend.core.repository.supabase.models.EventEntity
import com.codehavenx.alpaca.backend.core.service.models.Event
import com.codehavenx.alpaca.backend.core.service.models.requests.CreateEventRequest
import com.codehavenx.alpaca.backend.core.service.models.requests.GetEventRequest
import com.codehavenx.alpaca.backend.core.service.models.requests.GetEventsInRangeRequest
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Database for managing calendar events.
 */
class SupabaseCalendarDatabase(
    private val postgrest: Postgrest,
) : CalendarDatabase {

    /**
     * Create an event with the given parameters.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createEvent(
        request: CreateEventRequest,
    ): Result<Event> = runSuspendCatching(TAG) {
        logD(TAG, "Creating event")
        val requestEntity = request.toEventEntity()

        val createdEvent = postgrest.from(EventEntity.COLLECTION).insert(requestEntity) {
            select()
        }
            .decodeSingle<EventEntity>()

        logD(TAG, "Event %s", createdEvent.id)
        createdEvent.toEvent(request.timeZone)
    }

    /**
     * Get an event by its ID.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getEvent(
        request: GetEventRequest,
    ): Result<Event?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting event: %s", request.eventId)

        postgrest.from(EventEntity.COLLECTION).select {
            filter {
                EventEntity::id eq request.eventId
            }
            limit(1)
            single()
        }.decodeAsOrNull<EventEntity>()?.toEvent(request.timeZone)
    }

    /**
     * Get all events in the given time range.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getEventsInRange(
        request: GetEventsInRangeRequest,
    ): Result<List<Event>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting events in range: %s - %s", request.startTime, request.endTime)

        postgrest.from(EventEntity.COLLECTION).select {
            filter {
                EventEntity::endTime gte request.startTime.toLong(request.timeZone)
                EventEntity::startTime lte request.endTime.toLong(request.timeZone)
                if (request.owners.isNotEmpty()) {
                    and {
                        request.owners.forEach {
                            EventEntity::owner eq it.staffId
                        }
                    }
                }
            }
        }.decodeList<EventEntity>().map { it.toEvent(request.timeZone) }
    }

    companion object {
        const val TAG = "SupabaseCalendarDatabase"
    }
}
