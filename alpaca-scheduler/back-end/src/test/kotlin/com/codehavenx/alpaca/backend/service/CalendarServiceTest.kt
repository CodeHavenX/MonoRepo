package com.codehavenx.alpaca.backend.service

import com.codehavenx.alpaca.backend.core.repository.CalendarDatabase
import com.codehavenx.alpaca.backend.core.service.CalendarService
import com.codehavenx.alpaca.backend.core.service.models.Event
import com.codehavenx.alpaca.backend.core.service.models.StaffId
import com.codehavenx.alpaca.backend.core.service.models.requests.GetEventsInRangeRequest
import com.cramsan.framework.test.CoroutineTest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class CalendarServiceTest : CoroutineTest() {

    @BeforeTest
    fun setupTest() = Unit

    @Test
    fun `test getAvailableTimeSlots for staff`() = runCoroutineTest {
        val calendarDatabase = mockk<CalendarDatabase>()
        val calendarService = CalendarService(
            calendarDatabase = calendarDatabase
        )

        val startDate = LocalDateTime(2024, 1, 1, 0, 0)
        val endDate = LocalDateTime(2024, 1, 1, 10, 0)

        coEvery {
            calendarDatabase.getEventsInRange(
                GetEventsInRangeRequest(
                    startDate,
                    endDate,
                    listOf(StaffId("testOwner")),
                    TimeZone.UTC,
                )
            )
        } returns Result.success(
            listOf(
                createEvent(
                    LocalDateTime(2024, 1, 1, 0, 0),
                    LocalDateTime(2024, 1, 1, 0, 5),
                ),
                createEvent(
                    LocalDateTime(2024, 1, 1, 2, 0),
                    LocalDateTime(2024, 1, 1, 3, 0),
                ),
                createEvent(
                    LocalDateTime(2024, 1, 1, 4, 55),
                    LocalDateTime(2024, 1, 1, 5, 15),
                ),
                createEvent(
                    LocalDateTime(2024, 1, 1, 6, 10),
                    LocalDateTime(2024, 1, 1, 8, 32),
                ),
            )
        )

        val availability = calendarService.getAvailableTimeSlotsForStaff(
            startTime = startDate,
            endTime = endDate,
            timeZone = TimeZone.UTC,
            duration = 15.minutes,
            owner = StaffId("testOwner"),
        )

        assertEquals(availability.size, 22)
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T00:15, endTime=2024-01-01T00:30)",
            availability[0].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T00:30, endTime=2024-01-01T00:45)",
            availability[1].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T00:45, endTime=2024-01-01T01:00)",
            availability[2].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T01:00, endTime=2024-01-01T01:15)",
            availability[3].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T01:15, endTime=2024-01-01T01:30)",
            availability[4].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T01:30, endTime=2024-01-01T01:45)",
            availability[5].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T01:45, endTime=2024-01-01T02:00)",
            availability[6].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T03:00, endTime=2024-01-01T03:15)",
            availability[7].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T03:15, endTime=2024-01-01T03:30)",
            availability[8].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T03:30, endTime=2024-01-01T03:45)",
            availability[9].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T03:45, endTime=2024-01-01T04:00)",
            availability[10].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T04:00, endTime=2024-01-01T04:15)",
            availability[11].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T04:15, endTime=2024-01-01T04:30)",
            availability[12].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T04:30, endTime=2024-01-01T04:45)",
            availability[13].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T05:15, endTime=2024-01-01T05:30)",
            availability[14].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T05:30, endTime=2024-01-01T05:45)",
            availability[15].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T05:45, endTime=2024-01-01T06:00)",
            availability[16].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T08:45, endTime=2024-01-01T09:00)",
            availability[17].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T09:00, endTime=2024-01-01T09:15)",
            availability[18].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T09:15, endTime=2024-01-01T09:30)",
            availability[19].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T09:30, endTime=2024-01-01T09:45)",
            availability[20].toString()
        )
        assertEquals(
            "TimeSlot(staff=StaffId(staffId=testOwner), startTime=2024-01-01T09:45, endTime=2024-01-01T10:00)",
            availability[21].toString()
        )
    }

    @Test
    fun `test getAvailableTimeSlots for multiple staff`() = runCoroutineTest {
        val calendarDatabase = mockk<CalendarDatabase>()
        val calendarService = CalendarService(
            calendarDatabase = calendarDatabase
        )

        val startDate = LocalDateTime(2024, 1, 1, 0, 0)
        val endDate = LocalDateTime(2024, 1, 1, 2, 0)

        coEvery {
            calendarDatabase.getEventsInRange(
                GetEventsInRangeRequest(
                    startDate,
                    endDate,
                    listOf(StaffId("testOwner1")),
                    TimeZone.UTC,
                )
            )
        } returns Result.success(
            listOf(
                createEvent(
                    LocalDateTime(2024, 1, 1, 0, 0),
                    LocalDateTime(2024, 1, 1, 0, 5),
                ),
            )
        )

        coEvery {
            calendarDatabase.getEventsInRange(
                GetEventsInRangeRequest(
                    startDate,
                    endDate,
                    listOf(StaffId("testOwner2")),
                    TimeZone.UTC,
                )
            )
        } returns Result.success(
            listOf(
                createEvent(
                    LocalDateTime(2024, 1, 1, 0, 10),
                    LocalDateTime(2024, 1, 1, 0, 32),
                ),
            )
        )

        coEvery {
            calendarDatabase.getEventsInRange(
                GetEventsInRangeRequest(
                    startDate,
                    endDate,
                    listOf(StaffId("testOwner3")),
                    TimeZone.UTC,
                )
            )
        } returns Result.success(
            listOf(
                createEvent(
                    LocalDateTime(2024, 1, 1, 0, 55),
                    LocalDateTime(2024, 1, 1, 1, 25),
                ),
            )
        )

        val availability = calendarService.getAvailableTimeSlotsForStaff(
            startTime = startDate,
            endTime = endDate,
            timeZone = TimeZone.UTC,
            duration = 15.minutes,
            owners = listOf(StaffId("testOwner1"), StaffId("testOwner2"), StaffId("testOwner3")),
        )

        assertEquals(availability.size, 8)
        assertEquals(
            availability[LocalDateTime.parse("2024-01-01T00:00")].toString(),
            "[TimeSlot(staff=StaffId(staffId=testOwner3), startTime=2024-01-01T00:00, endTime=2024-01-01T00:15)]"
        )
        assertEquals(
            availability[LocalDateTime.parse("2024-01-01T00:15")].toString(),
            "[TimeSlot(staff=StaffId(staffId=testOwner1), startTime=2024-01-01T00:15, endTime=2024-01-01T00:30)," +
                " TimeSlot(staff=StaffId(staffId=testOwner3), startTime=2024-01-01T00:15, endTime=2024-01-01T00:30)]"
        )
        assertEquals(
            availability[LocalDateTime.parse("2024-01-01T00:30")].toString(),
            "[TimeSlot(staff=StaffId(staffId=testOwner1), startTime=2024-01-01T00:30, endTime=2024-01-01T00:45)," +
                " TimeSlot(staff=StaffId(staffId=testOwner3), startTime=2024-01-01T00:30, endTime=2024-01-01T00:45)]"
        )
        assertEquals(
            availability[LocalDateTime.parse("2024-01-01T00:45")].toString(),
            "[TimeSlot(staff=StaffId(staffId=testOwner1), startTime=2024-01-01T00:45, endTime=2024-01-01T01:00)," +
                " TimeSlot(staff=StaffId(staffId=testOwner2), startTime=2024-01-01T00:45, endTime=2024-01-01T01:00)]"
        )
        assertEquals(
            availability[LocalDateTime.parse("2024-01-01T01:00")].toString(),
            "[TimeSlot(staff=StaffId(staffId=testOwner1), startTime=2024-01-01T01:00, endTime=2024-01-01T01:15)," +
                " TimeSlot(staff=StaffId(staffId=testOwner2), startTime=2024-01-01T01:00, endTime=2024-01-01T01:15)]"
        )
        assertEquals(
            availability[LocalDateTime.parse("2024-01-01T01:15")].toString(),
            "[TimeSlot(staff=StaffId(staffId=testOwner1), startTime=2024-01-01T01:15, endTime=2024-01-01T01:30)," +
                " TimeSlot(staff=StaffId(staffId=testOwner2), startTime=2024-01-01T01:15, endTime=2024-01-01T01:30)]"
        )
        assertEquals(
            availability[LocalDateTime.parse("2024-01-01T01:30")].toString(),
            "[TimeSlot(staff=StaffId(staffId=testOwner1), startTime=2024-01-01T01:30, endTime=2024-01-01T01:45)," +
                " TimeSlot(staff=StaffId(staffId=testOwner2), startTime=2024-01-01T01:30, endTime=2024-01-01T01:45)," +
                " TimeSlot(staff=StaffId(staffId=testOwner3), startTime=2024-01-01T01:30, endTime=2024-01-01T01:45)]"
        )
        assertEquals(
            availability[LocalDateTime.parse("2024-01-01T01:45")].toString(),
            "[TimeSlot(staff=StaffId(staffId=testOwner1), startTime=2024-01-01T01:45, endTime=2024-01-01T02:00)," +
                " TimeSlot(staff=StaffId(staffId=testOwner2), startTime=2024-01-01T01:45, endTime=2024-01-01T02:00)," +
                " TimeSlot(staff=StaffId(staffId=testOwner3), startTime=2024-01-01T01:45, endTime=2024-01-01T02:00)]"
        )
    }

    private fun createEvent(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
    ): Event {
        return Event(
            id = "$startDateTime-$endDateTime",
            owner = StaffId(""),
            attendants = mockk(),
            title = "",
            description = "",
            startDateTime = startDateTime,
            endDateTime = endDateTime,
        )
    }
}
