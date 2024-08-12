package com.codehavenx.alpaca.backend.storage

import com.codehavenx.alpaca.backend.models.Event
import com.codehavenx.alpaca.backend.models.StaffId
import com.cramsan.framework.logging.logD
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

@Suppress("UnusedParameter", "UnusedPrivateProperty", "RedundantSuspendModifier")
class CalendarDatabase {

    suspend fun createEvent(
        owner: StaffId,
        attendants: Set<String>,
        title: String,
        description: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        timeZone: TimeZone,
    ): Event {
        logD(TAG, "Creating event")
        /*
        val eventEntity = createEventEntity(
            owner,
            attendants,
            title,
            description,
            startTime,
            endTime,
            timeZone,
            objectIdProvider,
        )
        val result = collection.insertOne(eventEntity)
        logD(TAG, "Event %S, created = %S", eventEntity.id, result.wasAcknowledged())
        if (result.wasAcknowledged()) {
            return eventEntity.toEvent(timeZone)
        } else {
            TODO()
        }
         */
        TODO()
    }

    suspend fun getEvent(
        eventId: String,
        timeZone: TimeZone,
    ): Event? {
        logD(TAG, "Getting event: %S", eventId)
        /*
        return collection
            .find(Filters.eq("_id", ObjectId(eventId)))
            .firstOrNull()?.toEvent(timeZone)
         */
        TODO()
    }

    suspend fun getEventsInRange(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        owners: List<StaffId>,
        timeZone: TimeZone,
    ): List<Event> {
        logD(TAG, "Getting events in range: %S - %S", startTime, endTime)
        /*
        val dateTimeFilter = Filters.and(
            Filters.gte("endTime", startTime.toLong(timeZone)),
            Filters.lte("startTime", endTime.toLong(timeZone)),
        )
        val filter = if (owners.isNotEmpty()) {
            Filters.and(
                Filters.or(
                    owners.map { Filters.eq("owner", it.staffId) },
                ),
                dateTimeFilter,
            )
        } else {
            dateTimeFilter
        }
        return collection
            .find(filter).toList().map { it.toEvent(timeZone) }
         */
        TODO()
    }

    suspend fun updateEvent(event: Event, timeZone: TimeZone): Boolean {
        logD(TAG, "Updating event: %S", event.id)
        /*
        val updatedEvent = event.toEventEntity(timeZone)
        return collection.replaceOne(
            Filters.eq("_id", updatedEvent.id),
            updatedEvent,
        ).wasAcknowledged()
         */
        TODO()
    }

    suspend fun deleteEvent(eventId: String): Boolean {
        logD(TAG, "Deleting event: %S", eventId)
        /*
        return collection.deleteOne(
            Filters.eq("_id", ObjectId(eventId)),
        ).wasAcknowledged()
         */
        TODO()
    }

    companion object {
        private const val TAG = "CalendarDatabase"
        private const val COLLECTION_NAME = "Events"
    }
}
