package com.codehavenx.alpaca.backend.service

import com.codehavenx.alpaca.backend.models.Event
import com.codehavenx.alpaca.backend.models.StaffId
import com.codehavenx.alpaca.backend.models.TimeSlot
import com.codehavenx.alpaca.backend.storage.CalendarDatabase
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration

class CalendarService(
    private val calendarDatabase: CalendarDatabase,
) {

    suspend fun createEvent(
        owner: StaffId,
        attendants: Set<String>,
        title: String,
        description: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        timeZone: TimeZone,
    ) {
        calendarDatabase.createEvent(
            owner,
            attendants,
            title,
            description,
            startTime,
            endTime,
            timeZone,
        )
    }

    suspend fun getEvents(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        timeZone: TimeZone,
        owners: List<StaffId> = emptyList(),
    ): List<Event> {
        return calendarDatabase.getEventsInRange(
            startTime = startTime,
            endTime = endTime,
            owners = owners,
            timeZone = timeZone,
        )
    }

    suspend fun getEvent(
        eventId: String,
        timeZone: TimeZone,
    ): Event? {
        return calendarDatabase.getEvent(eventId, timeZone)
    }

    suspend fun deleteEvent(
        eventId: String,
    ): Boolean {
        return calendarDatabase.deleteEvent(eventId)
    }

    suspend fun getAvailableTimeSlots(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        timeZone: TimeZone,
        duration: Duration,
        owners: List<StaffId>,
    ): Map<LocalDateTime, List<TimeSlot>> {
        val availabilityMap = owners.map {
            getAvailableTimeSlots(
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

    @Suppress("LoopWithTooManyJumpStatements")
    suspend fun getAvailableTimeSlots(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        timeZone: TimeZone,
        duration: Duration,
        owner: StaffId,
    ): List<TimeSlot> {
        val events = calendarDatabase.getEventsInRange(
            startTime = startTime,
            endTime = endTime,
            owners = listOf(owner),
            timeZone = timeZone,
        ).sortedBy { it.startDateTime }

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

    companion object {
        @Suppress("UnusedPrivateProperty")
        private const val TAG = "CalendarService"
    }
}
