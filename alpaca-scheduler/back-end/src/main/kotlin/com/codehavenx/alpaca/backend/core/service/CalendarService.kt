package com.codehavenx.alpaca.backend.core.service

import com.codehavenx.alpaca.backend.core.repository.CalendarDatabase
import com.codehavenx.alpaca.backend.core.service.models.Event
import com.codehavenx.alpaca.backend.core.service.models.StaffId
import com.codehavenx.alpaca.backend.core.service.models.TimeSlot
import com.codehavenx.alpaca.backend.core.service.models.requests.CreateEventRequest
import com.codehavenx.alpaca.backend.core.service.models.requests.GetEventRequest
import com.codehavenx.alpaca.backend.core.service.models.requests.GetEventsInRangeRequest
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Service for managing calendar events. This service is responsible for creating and retrieving events.
 */
class CalendarService(
    private val calendarDatabase: CalendarDatabase,
) {

    /**
     * Create an event with the given parameters.
     */
    suspend fun createEvent(
        owner: StaffId,
        attendants: Set<String>,
        title: String,
        description: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        timeZone: TimeZone,
    ): Event {
        return calendarDatabase.createEvent(
            CreateEventRequest(
                owner = owner,
                attendants = attendants,
                title = title,
                description = description,
                startTime = startTime,
                endTime = endTime,
                timeZone = timeZone,
            )
        ).getOrThrow()
    }

    /**
     * Get all events in the given time range.
     */
    suspend fun getEvents(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        timeZone: TimeZone,
        owners: List<StaffId>,
    ): List<Event> {
        return calendarDatabase.getEventsInRange(
            GetEventsInRangeRequest(
                startTime = startTime,
                endTime = endTime,
                owners = owners,
                timeZone = timeZone,
            )
        ).getOrThrow()
    }

    /**
     * Get the event with the given ID.
     */
    suspend fun getEvent(
        eventId: String,
        timeZone: TimeZone,
    ): Event? {
        return calendarDatabase.getEvent(GetEventRequest(eventId, timeZone)).getOrThrow()
    }

    /**
     * Get the available time slots for the given staff members between the given start and end times.
     */
    suspend fun getAvailableTimeSlotsForStaff(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        timeZone: TimeZone,
        duration: Duration,
        owners: List<StaffId>,
    ): Map<LocalDateTime, List<TimeSlot>> {
        val availabilityMap = owners.map {
            getAvailableTimeSlotsForStaff(
                startTime = startTime,
                endTime = endTime,
                timeZone = timeZone,
                duration = duration,
                owner = it,
            )
        }

        val sortedTimeSlots = availabilityMap.flatten().groupBy { it.startTime }
        return sortedTimeSlots
    }

    /**
     * Get the available time slots for the given staff member between the given start and end times.
     */
    @OptIn(ExperimentalTime::class)
    @Suppress("LoopWithTooManyJumpStatements")
    suspend fun getAvailableTimeSlotsForStaff(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        timeZone: TimeZone,
        duration: Duration,
        owner: StaffId,
    ): List<TimeSlot> {
        val events = calendarDatabase.getEventsInRange(
            GetEventsInRangeRequest(
                startTime = startTime,
                endTime = endTime,
                owners = listOf(owner),
                timeZone = timeZone,
            )
        ).getOrThrow().sortedBy { it.startDateTime }

        if (events.isEmpty()) {
            return emptyList()
        }

        val availableTimeSlots = mutableListOf<TimeSlot>()
        var currentStartTime = startTime
        var lowerBoundEvent = 0
        do {
            val slotStartTime = currentStartTime
            val slotEndTime = (slotStartTime.toInstant(timeZone) + duration).toLocalDateTime(timeZone)

            val eventsToCompare = events.subList(lowerBoundEvent, events.size)

            var conflictFound = false
            for (event in eventsToCompare) {
                if (event.endDateTime <= slotStartTime) {
                    // there is no conflict. The event ends before the slot starts.
                    // So we can move to the next event.
                    lowerBoundEvent++
                } else if (event.startDateTime >= slotEndTime) {
                    // there is no conflict. The event starts after the slot ends.
                    break
                } else {
                    conflictFound = true
                    break
                }
            }
            if (!conflictFound) {
                val timeSlot = TimeSlot(
                    staff = owner,
                    startTime = slotStartTime,
                    endTime = slotEndTime,
                )
                availableTimeSlots.add(timeSlot)
            }

            currentStartTime = slotEndTime
        } while (currentStartTime < endTime)

        return availableTimeSlots.toList()
    }
}
