package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.server.datastore.EventLogDatastore
import com.cramsan.edifikana.server.service.models.EventLogEntry
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Test class for [EventLogService].
 */
@OptIn(ExperimentalTime::class)
class EventLogServiceTest {
    private lateinit var eventLogDatastore: EventLogDatastore
    private lateinit var eventLogService: EventLogService

    /**
     * Sets up the test environment by initializing mocks for [EventLogDatastore] and [eventLogService].
     */
    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        eventLogDatastore = mockk()
        eventLogService = EventLogService(eventLogDatastore)
    }

    /**
     * Cleans up the test environment by stopping Koin.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    /**
     * Tests that createEventLogEntry creates an event log entry and returns it.
     */
    @Test
    fun `createEventLogEntry should call database and return entry`() = runTest {
        // Arrange
        val employeeId = EmployeeId("employee-1")
        val fallbackEmployeeName = "John Doe"
        val propertyId = PropertyId("property-1")
        val type = EventLogEventType.INCIDENT
        val fallbackEventType = "incident"
        val timestamp = Instant.parse("2024-06-18T12:00:00Z")
        val title = "Burst Pipe"
        val description = "Pipe burst in apartment 1608 resulting in a minor flooding that has affected..."
        val unit = "1608"
        val entry = mockk<EventLogEntry>()
        coEvery {
            eventLogDatastore.createEventLogEntry(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns Result.success(entry)

        // Act
        val result = eventLogService.createEventLogEntry(
            employeeId,
            fallbackEmployeeName,
            propertyId,
            type,
            fallbackEventType,
            timestamp,
            title,
            description,
            unit,
        )

        // Assert
        assertEquals(entry, result)
        coVerify {
            eventLogDatastore.createEventLogEntry(
                employeeId,
                fallbackEmployeeName,
                propertyId,
                type,
                fallbackEventType,
                timestamp,
                title,
                description,
                unit,
            )
        }
    }

    /**
     * Tests that getEventLogEntry retrieves an event log entry by ID and returns it.
     */
    @Test
    fun `getEventLogEntry should call database and return entry`() = runTest {
        // Arrange
        val entryId = EventLogEntryId("entry-1")
        val entry = mockk<EventLogEntry>()
        coEvery { eventLogDatastore.getEventLogEntry(any()) } returns Result.success(entry)

        // Act
        val result = eventLogService.getEventLogEntry(entryId)

        // Assert
        assertEquals(entry, result)
        coVerify { eventLogDatastore.getEventLogEntry(entryId) }
    }

    /**
     * Tests that getEventLogEntry returns null if the entry is not found.
     */
    @Test
    fun `getEventLogEntry should return null if not found`() = runTest {
        // Arrange
        val entryId = EventLogEntryId("entry-2")
        coEvery { eventLogDatastore.getEventLogEntry(any()) } returns Result.failure(Exception("Not found"))

        // Act
        val result = eventLogService.getEventLogEntry(entryId)

        // Assert
        assertNull(result)
        coVerify { eventLogDatastore.getEventLogEntry(entryId) }
    }

    /**
     * Tests that getEventLogEntries retrieves all event log entries and returns a list.
     */
    @Test
    fun `getEventLogEntries should call database and return list`() = runTest {
        // Arrange
        val propertyId = PropertyId("property1")
        val entryList = listOf(mockk<EventLogEntry>(), mockk<EventLogEntry>())
        coEvery { eventLogDatastore.getEventLogEntries(propertyId) } returns Result.success(entryList)

        // Act
        val result = eventLogService.getEventLogEntries(propertyId)

        // Assert
        assertEquals(entryList, result)
        coVerify { eventLogDatastore.getEventLogEntries(propertyId) }
    }

    /**
     * Tests that updateEventLogEntry updates an event log entry and returns the updated entry.
     */
    @Test
    fun `updateEventLogEntry should call database and return updated entry`() = runTest {
        // Arrange
        val entryId = EventLogEntryId("entry-3")
        val type = EventLogEventType.GUEST
        val fallbackEventType = "guest"
        val title = "Guest for apt 1801"
        val description = "Jenny Hall visiting"
        val unit = "1801"
        val entry = mockk<EventLogEntry>()
        coEvery {
            eventLogDatastore.updateEventLogEntry(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns Result.success(entry)

        // Act
        val result = eventLogService.updateEventLogEntry(entryId, type, fallbackEventType, title, description, unit)

        // Assert
        assertEquals(entry, result)
        coVerify {
            eventLogDatastore.updateEventLogEntry(
                entryId,
                type,
                fallbackEventType,
                title,
                description,
                unit,
            )
        }
    }

    /**
     * Tests that deleteEventLogEntry deletes an event log entry and returns true.
     */
    @Test
    fun `deleteEventLogEntry should call database and return true`() = runTest {
        // Arrange
        val entryId = EventLogEntryId("entry-4")
        coEvery { eventLogDatastore.deleteEventLogEntry(any()) } returns Result.success(true)

        // Act
        val result = eventLogService.deleteEventLogEntry(entryId)

        // Assert
        assertEquals(true, result)
        coVerify { eventLogDatastore.deleteEventLogEntry(entryId) }
    }
}
