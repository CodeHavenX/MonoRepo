package com.codehavenx.alpaca.backend.storage

import com.codehavenx.alpaca.backend.models.StaffId
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.test.TestBase
import com.mongodb.ConnectionString
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.bson.types.ObjectId
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class CalendarDatabaseTest : TestBase() {

    private val connectionString = ConnectionString("mongodb://localhost:27017")
    private val mongoClient = MongoClient.create(connectionString)
    private val database = mongoClient.getDatabase("test")

    private val testObjectId = ObjectId("65bb3d1cce761d0352aee080")
    private val timeZone = TimeZone.UTC

    override fun setupTest() {
        EventLogger.setInstance(
            mockk(
                relaxed = true,
            )
        )

        runBlocking {
            database.drop()
        }
    }

    @Ignore
    @Test
    fun `test crud operations`() = runBlockingTest {
        val startDate = LocalDateTime(2024, 1, 1, 0, 0)
        val endDate = LocalDateTime(2024, 1, 1, 1, 0)
        val objectIdProvider: () -> ObjectId = { testObjectId }
        val calendarDatabase = CalendarDatabase(database, objectIdProvider)

        // Create event
        val event = calendarDatabase.createEvent(
            owner = StaffId("testOwner"),
            attendants = setOf("testAttendant"),
            title = "testTitle",
            description = "testDescription",
            startTime = startDate,
            endTime = endDate,
            timeZone = timeZone,
        )

        // Fetch event
        val fetchedEvent = calendarDatabase.getEvent(
            eventId = event.id,
            timeZone = timeZone,
        )
        assertEquals(event, fetchedEvent)

        // Update event
        val modifiedEvent = fetchedEvent!!.copy(
            title = "modifiedTitle",
            description = "modifiedDescription",
        )
        assertTrue(calendarDatabase.updateEvent(modifiedEvent, timeZone))
        val fetchedUpdatedEvent = calendarDatabase.getEvent(
            eventId = event.id,
            timeZone = timeZone,
        )
        assertEquals(modifiedEvent, fetchedUpdatedEvent)

        // Delete event
        assertTrue(calendarDatabase.deleteEvent(eventId = event.id))
        assertNull(calendarDatabase.getEvent(eventId = event.id, timeZone = timeZone))
    }

    @Ignore
    @Test
    fun `test getting events in range`() = runBlockingTest {
        val objectIdProvider: () -> ObjectId = { ObjectId() }
        val calendarDatabase = CalendarDatabase(database, objectIdProvider)
        val currentDate = LocalDateTime(2024, 1, 1, 0, 0)

        // Create events
        repeat(100) {
            // Create events with duration of 60 minutes and in intervals of 30 minutes
            val eventStartDateTime = currentDate.toInstant(timeZone) + (30.minutes * it)
            val eventEndDateTime = currentDate.toInstant(timeZone) + (30.minutes * it + 60.minutes)

            calendarDatabase.createEvent(
                owner = StaffId("testOwner"),
                attendants = emptySet(),
                title = "Event $it",
                description = "Description $it",
                startTime = eventStartDateTime.toLocalDateTime(timeZone),
                endTime = eventEndDateTime.toLocalDateTime(timeZone),
                timeZone = timeZone,
            )
        }

        val events = calendarDatabase.getEventsInRange(
            startTime = LocalDateTime(2024, 1, 1, 1, 15),
            endTime = LocalDateTime(2024, 1, 1, 3, 10),
            owners = emptyList(),
            timeZone = timeZone,
        )
        assertEquals(6, events.size)
    }

    @Ignore
    @Test
    fun `test getting events in range with owner`() = runBlockingTest {
        val objectIdProvider: () -> ObjectId = { ObjectId() }
        val calendarDatabase = CalendarDatabase(database, objectIdProvider)
        val currentDate = LocalDateTime(2024, 1, 1, 0, 0)

        // Create events
        repeat(100) {
            // Create events with duration of 60 minutes and in intervals of 30 minutes
            val eventStartDateTime = currentDate.toInstant(timeZone) + (30.minutes * it)
            val eventEndDateTime = currentDate.toInstant(timeZone) + (30.minutes * it + 60.minutes)

            calendarDatabase.createEvent(
                owner = StaffId("testOwner-${it % 3}"),
                attendants = emptySet(),
                title = "Event $it",
                description = "Description $it",
                startTime = eventStartDateTime.toLocalDateTime(timeZone),
                endTime = eventEndDateTime.toLocalDateTime(timeZone),
                timeZone = timeZone,
            )
        }

        val events = calendarDatabase.getEventsInRange(
            startTime = LocalDateTime(2024, 1, 1, 1, 15),
            endTime = LocalDateTime(2024, 1, 1, 3, 10),
            owners = listOf(StaffId("testOwner-0"), StaffId("testOwner-2")),
            timeZone = timeZone,
        )
        assertEquals(4, events.size)
    }
}
